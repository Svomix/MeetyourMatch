'use client';
import { useRef } from 'react';
import styles from './index.module.css';
import { getIsLoggedIn } from '@/services/authService';

export default ({ onClick }) => {
  let textarea = useRef();

  return (
    <div className={styles.wrapper}>
      <textarea
        className={styles.input}
        ref={textarea}
        type="text"
        disabled={!getIsLoggedIn()}
        placeholder="Оставить комментарий..."
      />
      <div className={styles.buttons}>
        <button
          className={styles.submit_btn}
          type="submit"
          disabled={!getIsLoggedIn()}
          onClick={async function (e) {
            e.text = textarea.current.value;
            await onClick(e);
            textarea.current.value = '';
          }}
        >
          Отправить
        </button>
      </div>
    </div>
  );
};
