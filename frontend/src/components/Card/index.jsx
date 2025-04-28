import Heart from '@components/Buttons/HeartButton';
import mock_event_img from '@public/mock_event_img.gif';
import Image from 'next/image';
import Link from 'next/link';
import styles from './index.module.css';
import { getIsLoggedIn } from '@/services/authService';
import { useDispatch } from 'react-redux';
import { ModalPage, setModal } from '@store/modalSlice/index';
import { authed } from '@/services/axiosInstance';
import { useEffect, useState } from 'react';

export default ({ event, refLink, width, height }) => {
  const dispatch = useDispatch();
  let is_logged = getIsLoggedIn();
  let [liked, setLiked] = useState(false);
  let [likeCount, setLikeCount] = useState();

  useEffect(() => {
    event.userAction && setLiked(event.userAction?.isLiked);
    setLikeCount(event?.userActionCounters.likedCounter);
  }, [event]);

  const heart_click = (e) => {
    e.preventDefault();
    setLikeCount((prev) => prev + liked * -2 + 1);
    if (is_logged) {
      authed.post(`/account/events/${event.id}/like`).then((resp) => {
        setLiked(resp.data.isLiked);
      });
    } else dispatch(setModal(ModalPage.Login));
  };
  return (
    <Link
      href={`/events/${event.id}`}
      className={styles.card}
      style={{ width: `${width}px`, height: `${height}px` }}
      ref={refLink}
    >
      <div className={styles.img_container}>
        <Image
          className={styles.image}
          src={event.coverImgUrl || mock_event_img}
          width={1024}
          height={1024}
          alt="img"
        />
        <div className={styles.fade_out} />
      </div>

      <div className={styles.description}>
        <div className={styles.event_title}>{event.title}</div>
        {event.date && (
          <p className={styles.event_date}>{new Date(event.date).toLocaleString('ru-RU')}</p>
        )}
        {event.tags && (
          <p className={styles.event_tags}>{event.tags.map((t) => '#' + t.name).join(' ')}</p>
        )}
        <div className={styles.heart_wrapper}>
          <span className={styles.heart_counter}>{likeCount}</span>
          <Heart
            width={40}
            height={40}
            className={styles.heart}
            active={liked}
            onClick={heart_click}
          />
        </div>
      </div>
    </Link>
  );
};
