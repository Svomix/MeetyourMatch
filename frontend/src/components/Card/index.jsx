import Heart from '@components/Buttons/HeartButton';
import mock_event_img from '@public/mock_event_img.jpg';
import Image from 'next/image';
import Link from 'next/link';
import styles from './index.module.css';

export default ({ event, refLink, width, height }) => {
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
        <p className={styles.event_date}>{new Date(event.date || 0).toLocaleString('ru-RU')}</p>
        <p className={styles.event_tags}>#отдых #искусство</p>
        <Heart width={40} height={40} className={styles.heart} />
      </div>
    </Link>
  );
};
