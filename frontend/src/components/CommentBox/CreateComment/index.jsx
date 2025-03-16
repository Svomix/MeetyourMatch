import styles from './index.module.css';

export default () => {
  return (
    <from className={styles.wrapper}>
      <textarea className={styles.input} type="text" placeholder="Оставить комментарий..." />
      <div className={styles.buttons}>
        <div className={styles.rating} />
        <button className={styles.submit_btn} type="submit">
          Отправить
        </button>
      </div>
    </from>
  );
};
