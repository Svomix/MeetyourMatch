'use client';
import styles from './index.module.css';

export default () => {
  return (
    <section className={styles.search} onClick={(e) => e.stopPropagation()}>
      <input className={styles.input} placeholder={'поиск по названию или #тегу'} id="searchBar" />
      <div className={styles.border} />
      <button className={styles.magnifier}>
        <Magnifier />
      </button>
    </section>
  );
};

const Magnifier = () => (
  <svg width="20" height="20" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
    <path
      fill="currentColor"
      d="M0 8.32315C0 12.9137 3.73464 16.6442 8.32105 16.6442C10.4341 16.6442 12.3669 15.8498 13.837 14.5476L19.14 19.8506C19.2383 19.9489 19.3653 19.998 19.4963 19.998C19.6274 19.998 19.7543 19.9489 19.8526 19.8506C20.0491 19.654 20.0491 19.3387 19.8526 19.1422L14.5455 13.8391C15.8477 12.369 16.6421 10.4403 16.6421 8.32315C16.6421 3.73265 12.9075 0.00210571 8.32105 0.00210571C3.73464 0.00210571 0 3.73265 0 8.32315ZM15.6388 8.32315C15.6388 12.3567 12.3546 15.6409 8.32105 15.6409C4.28747 15.6409 1.00328 12.3567 1.00328 8.32315C1.00328 4.28958 4.28747 1.00538 8.32105 1.00538C12.3546 1.00538 15.6388 4.28548 15.6388 8.32315Z"
    />
  </svg>
);