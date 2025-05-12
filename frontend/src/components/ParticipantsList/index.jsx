import Link from 'next/link';
import styles from './index.module.css';

export default ({ participants }) => {
  return (
    <div className={styles.wrapper}>
      <h1 className={styles.added}>Добавили в календарь:</h1>
      <div className={styles.participants_wrapper}>
        {participants.map((p) => (
          <Link key={p.id} href={`/user/${p.id}`} className={styles.participant}>
            {p.username}
          </Link>
        ))}
      </div>
    </div>
  );
};
