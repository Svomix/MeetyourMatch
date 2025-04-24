'use client';
import Calendar from '@components/Buttons/CalendarButton';
import Heart from '@components/Buttons/HeartButton';
import mock_event_img from '@public/mock_event_img.jpg';
import Image from 'next/image';
import Link from 'next/link';
import styles from './index.module.css';
import CommentBox from '@components/CommentBox';
import { authed } from '@/services/axiosInstance';
import { useDispatch } from 'react-redux';
import { fetchEventInfo } from '@store/eventStore';
import { getIsLoggedIn } from '@/services/authService';
import { ModalPage, setModal } from '@store/modalSlice/index';
import { useEffect, useState } from 'react';

export default ({ path, event }) => {
  const dispatch = useDispatch();
  let is_logged = getIsLoggedIn();
  let [liked, setLiked] = useState(false);
  let [calendar, setCalendar] = useState(false);

  useEffect(() => {
    event.userAction && setLiked(event.userAction?.isLiked);
    event.userAction && setCalendar(event.userAction?.inCalendar);
  }, [event]);

  const heart_click = (e) => {
    e.preventDefault();
    if (is_logged) {
      authed.post(`/account/events/${event.id}/like`).then((resp) => {
        setLiked(resp.data.isLiked);
      });
    } else dispatch(setModal(ModalPage.Login));
  };

  const calendar_click = (e) => {
    e.preventDefault();
    if (is_logged) {
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

  console.log(event);

  return (
    <>
      {event && (
        <article className={styles.wrapper}>
          <div className={styles.img_section}>
            <div className={styles.left_side}>
              <h1 className={styles.title}>{event.title}</h1>
              <h2 className={styles.meta}>Дата: {new Date(event.date).toLocaleString('ru-RU')}</h2>
              <h3 className={styles.meta}>Место: {event?.location?.title}</h3>
              <h4 className={styles.meta}>Цена: {event.price}</h4>
              <div className={styles.actions}>
                <div className={styles.icons}>
                  <div className={styles.count_wrapper}>
                    <Heart width={50} height={50} active={liked} onClick={heart_click} />
                    <span className={styles.counter}>{event?.userActionCounters.likedCounter}</span>
                  </div>
                  <div className={styles.count_wrapper}>
                    <Calendar width={50} height={50} active={calendar} onClick={calendar_click} />
                    <span className={styles.counter}>
                      {event?.userActionCounters.calendarCounter}
                    </span>
                  </div>
                </div>
                {event.sourceUrl && <Link className={styles.link} href={event.sourceUrl} target="_blank">
                  К источнику
                </Link>}
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
            {event.tags && <p className={styles.tags}>{event.tags.map((t) => "#" + t.name).join(" ")}</p>}
          </div>
          <div className={styles.comments}>
            <CommentBox
              comments={comments_mapped}
              onSubmit={onSubmitComment}
              onDelete={onDeleteComment}
            />
          </div>
        </article>
      )}
    </>
  );
};
