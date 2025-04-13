import { LeftArrow, RightArrow } from '@components/Buttons/ArrowButtons';
import styles from './index.module.css';

export default ({ info }) => {
  return (
    <div className={styles.wrapper}>
      <LeftArrow width={48} height={48} />
      <h1 className={styles.info}>Месяц</h1>
      <h1 className={styles.info}>Год</h1>
      <RightArrow width={48} height={48} />
    </div>
  );
};
