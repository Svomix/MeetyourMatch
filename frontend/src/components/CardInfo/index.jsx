'use client';
import { getIsLoggedIn } from '@/services/authService';
import { authed } from '@/services/axiosInstance';
import BrokenHeart from '@components/Buttons/BrokenHeart';
import Calendar from '@components/Buttons/CalendarButton';
import Heart from '@components/Buttons/HeartButton';
import CommentBox from '@components/CommentBox';
import mock_event_img from '@public/mock_event_img.gif';
import { fetchEventInfo } from '@store/eventStore';
import { ModalPage, setModal } from '@store/modalSlice/index';
import Image from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import styles from './index.module.css';

export default ({ path, event }) => {
  const dispatch = useDispatch();
  const router = useRouter();
  let is_logged = getIsLoggedIn();
  let [liked, setLiked] = useState(false);
  let [disliked, setDisliked] = useState(false);
  let [calendar, setCalendar] = useState(false);
  let [likeCount, setLikeCount] = useState();
  let [dislikeCount, setDislikeCount] = useState();
  let [calendarCount, setCalendarCount] = useState();
  const is_admin = useSelector((state) => state.profileInfo)?.authorities.filter(
    (el) => el.authority == 'ROLE_ADMIN'
  ).length;
  const myId = useSelector((state) => state.profileInfo)?.id;
  const is_created_by_me = myId != null && myId == event?.createdBy?.id;
  console.log(event?.createdBy);

  useEffect(() => {
    event.userAction && setLiked(event.userAction?.isLiked);
    event.userAction && setDisliked(event.userAction?.isDisliked);
    event.userAction && setCalendar(event.userAction?.inCalendar);
    setLikeCount(event?.userActionCounters.likedCounter);
    setDislikeCount(event?.userActionCounters.dislikedCounter);
    setCalendarCount(event?.userActionCounters.calendarCounter);
  }, [event]);

  const heart_click = (e) => {
    e.preventDefault();

    if (is_logged) {
      setLikeCount((prev) => prev + liked * -2 + 1);
      authed.post(`/account/events/${event.id}/like`).then((resp) => {
        setLiked(resp.data.isLiked);
      });
    } else dispatch(setModal(ModalPage.Login));
  };

  const dislike_click = (e) => {
    e.preventDefault();

    if (is_logged) {
      setDislikeCount((prev) => prev + disliked * -2 + 1);
      authed.post(`/account/events/${event.id}/dislike`).then((resp) => {
        setDisliked(resp.data.isDisliked);
      });
    } else dispatch(setModal(ModalPage.Login));
  };

  const onDelete = () => {
    authed.delete(`v1/events/${event.id}`);
    router.back();
  };

  const calendar_click = (e) => {
    e.preventDefault();

    if (is_logged) {
      setCalendarCount((prev) => prev + calendar * -2 + 1);
      authed.post(`/account/events/${event.id}/calendar`).then((resp) => {
        setCalendar(resp.data.inCalendar);
      });
    } else dispatch(setModal(ModalPage.Login));
  };

  async function onSubmitComment(e) {
    try {
      await authed.post(`v1/events/${event.id}/comments`, {
        content: e.text
      });
    } catch (e) {
      alert(e);
    } finally {
      dispatch(fetchEventInfo(event.id));
    }
  }

  async function onDeleteComment(e) {
    try {
      await authed.delete(`v1/events/${event.id}/comments`, { params: { id: e.id } });
    } catch (e) {
      alert(e);
    } finally {
      dispatch(fetchEventInfo(event.id));
    }
  }

  const comments_mapped = event.comments.map((c) => {
    return {
      id: c.id,
      author: c.user.username,
      author_id: c.user.id,
      text: c.content,
      date: c.date
    };
  });

  return (
    <>
      {event && (
        <article className={styles.wrapper}>
          <div className={styles.img_section}>
            <div className={styles.left_side}>
              <h1 className={styles.title}>{event.title}</h1>
              <h2 className={styles.meta}>
                Дата: {event.date ? new Date(event.date).toLocaleString('ru-RU') : 'Не указана'}
              </h2>
              <h3 className={styles.meta}>Место: {event.location?.title || 'Не указано'}</h3>
              <h4 className={styles.meta}>Цена: {event.price || 'Не указана'}</h4>
              <div className={styles.actions}>
                <div className={styles.icons}>
                  <div className={styles.count_wrapper}>
                    <Heart width={50} height={50} active={liked} onClick={heart_click} />
                    <span className={styles.counter}>{likeCount}</span>
                  </div>
                  <div className={styles.count_wrapper}>
                    <BrokenHeart width={50} height={50} active={disliked} onClick={dislike_click} />
                    <span className={styles.counter}>{dislikeCount}</span>
                  </div>
                  <div className={styles.count_wrapper}>
                    <Calendar width={50} height={50} active={calendar} onClick={calendar_click} />
                    <span className={styles.counter}>{calendarCount}</span>
                  </div>
                </div>
                {event.sourceUrl && (
                  <Link className={styles.link} href={event.sourceUrl} target="_blank">
                    К источнику
                  </Link>
                )}
              </div>
            </div>
            <div className={styles.right_side}>
              <Image
                alt={'event image'}
                className={styles.img}
                src={event.coverImgUrl || mock_event_img}
                width={2000}
                height={2000}
              />
              <div className={styles.fade_out}></div>
            </div>
          </div>
          <div className={styles.extra_info}>
            <h4 className={styles.desc_name}>Описание:</h4>
            <p className={styles.description}>{event.description}</p>
            {event.tags && (
              <p className={styles.tags}>{event.tags.map((t) => '#' + t.name).join(' ')}</p>
            )}
            {event?.createdBy?.username && (
              <>
                <p className={styles.created_by}>Создано пользователем:</p>
                <Link className={styles.created_by_wrapper} href={'/user/' + event.createdBy.id}>
                  <Image
                    className={styles.created_by_img}
                    src={event.createdBy.avatarPath}
                    width={48}
                    height={48}
                  />
                  <h3 className={styles.name}>{event.createdBy.username}</h3>
                </Link>
              </>
            )}
          </div>
          <div className={styles.comments}>
            <CommentBox
              comments={comments_mapped}
              onSubmit={onSubmitComment}
              onDelete={onDeleteComment}
            />
          </div>
          {(!!is_admin || is_created_by_me) && (
            <button className={styles.delete_event} onClick={onDelete}>
              Удалить событие
            </button>
          )}
        </article>
      )}
    </>
  );
};
