import styles from './index.module.css';
import Link from 'next/link';

export default ({ comment }) => {
  console.log(comment);
  return (
    <div className={styles.wrapper}>
      <Link className={styles.author} href="/">
        {comment.author}
      </Link>
      <p className={styles.text}>{comment.text}</p>
    </div>
  );
};
