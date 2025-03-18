'use client';
import { useSelector } from 'react-redux';
import styles from './index.module.css';
import Link from 'next/link';

export default ({ comment, onDelete }) => {
  const user_id = useSelector((state) => state.profileInfo?.id);

  function formatDate(isoString) {
    const date = new Date(isoString);
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Месяцы с 0
    const year = date.getFullYear();

    return `${hours}:${minutes} ${day}.${month}.${year}`;
  }

  return (
    <div className={styles.wrapper}>
      <div className={styles.container}>
        <div className={styles.obertka}>
          <Link className={styles.author} href={`/user/${comment.author_id}`}>
            {comment.author}
          </Link>
          <span className={styles.date}>{new Date(comment.date).toLocaleString('ru-RU')}</span>
        </div>
        {user_id == comment.author_id && (
          <button onClick={(e) => onDelete({ id: comment.id, ...e })} className={styles.trash}>
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M3 6h18" />
              <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
              <path d="M10 11v6" />
              <path d="M14 11v6" />
              <path d="M5 6l1 14a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2l1-14" />
            </svg>
          </button>
        )}
      </div>
      <p className={styles.text}>{comment.text}</p>
    </div>
  );
};
