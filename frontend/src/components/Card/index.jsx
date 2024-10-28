import Heart from '@components/Heart';
import Image from 'next/image';
import Link from 'next/link';
import styles from './index.module.css';

export default ({ event, width, height }) => {
  return (
    <Link
      href={`/events/${event.id}`}
      className={styles.card}
      style={{ width: `${width}`, height: `${height}` }}
    >
      <div className={styles.img_container}>
        <Image className={styles.image} src={event.img} alt="img" />
        <div className={styles.fade_out} />
      </div>

      <div className={styles.description}>
        <h3 className={styles.event_title}>{event.title}</h3>
        <p className={styles.event_date}>{event.date}</p>
        <p className={styles.event_tags}>{event.tags}</p>
        <Heart width={40} height={40} className={styles.heart} />
      </div>
    </Link>
  );
};
