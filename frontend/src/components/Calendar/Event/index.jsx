import Link from 'next/link';
import styles from './index.module.css';

export default ({ event }) => {
  return (
    <Link href={`/events/${event.event.id}`} className={styles.event}>
      {event.event.title}
    </Link>
  );
};
