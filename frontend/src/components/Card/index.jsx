import people_icon from '@public/people_icon.svg';
import Image from 'next/image';
import Link from 'next/link';
import Heart from '../Heart';
import styles from './index.module.css';

export default ({ event, refLink }) => {
  return (
    <Link href={`/events/${event.id}`} className={styles.link}>
      <div className={styles.card} ref={refLink}>
        <div className={styles.options}>
          <div className={styles.heart_ui}>
            <Heart className={styles.icon} />
            <span className={styles.heart_counter}>52</span>
          </div>
          <Image className={styles.icon} src={people_icon} alt="people" />
        </div>

        <Image className={styles.image} src={event.img} alt="img" />
        <div className={styles.fade_out} />

        <div className={styles.description}>
          <h3 className={styles.event_title}>{event.title}</h3>
          <p className={styles.event_description}>{event.description}</p>
          <p className={styles.event_tags}>{event.tags}</p>
        </div>
      </div>
    </Link>
  );
};
