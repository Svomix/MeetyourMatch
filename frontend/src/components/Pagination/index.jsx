'use client';
import { LeftArrow, RightArrow } from '@components/Buttons/ArrowButtons';
import styles from './index.module.css';

export default () => {
  return (
    <div className={styles.wrapper}>
      <LeftArrow width={56} height={56} />
      <div className={styles.page}>1 ... 2 3 4 ... 9</div>
      <RightArrow width={56} height={56} />
    </div>
  );
};
