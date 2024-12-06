import Slider from '@components/Slider';
import styles from './index.module.css';

export default ({ events }) => {
  return (
    <div className={styles.wrapper}>
      <div className={styles.text}>
        <h1 className={styles.title}>Рекомендации</h1>
        <p className={styles.paragraph}>Возможно, Вам будет интересно</p>
      </div>

      <div className={styles.slider}>
        <Slider events={events} cardHeight={450} cardWidth={300} />
      </div>
    </div>
  );
};
