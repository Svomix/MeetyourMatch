'use client';
import { LeftArrow, RightArrow } from '@components/Buttons/ArrowButtons';
import styles from './index.module.css';

export default ({ current, setCurrent, total }) => {
  return (
    <div className={styles.wrapper}>
      <LeftArrow
        onClick={() => setCurrent(current - 1)}
        width={56}
        height={56}
        disabled={current == 1}
      />

      <div className={styles.pages}>
        {current > 2 && (
          <button className={styles.btn1} onClick={() => setCurrent(current - 2)}>
            {current - 2}
          </button>
        )}
        {current > 1 && (
          <button className={styles.btn2} onClick={() => setCurrent(current - 1)}>
            {current - 1}
          </button>
        )}
        <button className={styles.btn3} onClick={() => setCurrent(current)}>
          {current}
        </button>
        {current < total && (
          <button className={styles.btn4} onClick={() => setCurrent(current + 1)}>
            {current + 1}
          </button>
        )}
        {current < total - 1 && (
          <button className={styles.btn5} onClick={() => setCurrent(current + 2)}>
            {current + 2}
          </button>
        )}
      </div>

      <RightArrow
        onClick={() => setCurrent(current + 1)}
        width={56}
        height={56}
        disabled={current == total}
      />
    </div>
  );
};
