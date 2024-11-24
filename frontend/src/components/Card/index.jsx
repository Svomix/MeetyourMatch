import classNames from '@/utils/classnames';
import Heart from '@components/Buttons/HeartButton';
import Image from 'next/image';
import Link from 'next/link';
import styles from './index.module.css';

export default ({ className, event, width, height, ...rest }) => {
  return (
    <Link href={`/events/${event.id}`} className={classNames(styles.card, className)} {...rest}>
      <div className={styles.img_container}>
        <Image
          className={styles.image}
          src={event.coverImgUrl}
          width={1024}
          height={1024}
          alt="img"
        />
        <div className={styles.fade_out} />
      </div>

      <div className={styles.description}>
        <h3 className={styles.event_title}>{event.title}</h3>
        <p className={styles.event_date}>{new Date(event.date).toLocaleString('ru-RU')}</p>
        <p className={styles.event_tags}>#отдых #искусство</p>
        <Heart width={40} height={40} className={styles.heart} />
      </div>
    </Link>
  );
};
