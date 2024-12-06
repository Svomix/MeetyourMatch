import Slider from '@components/Slider';
import styles from './index.module.css';

export default ({ events }) => {
  return (
    <div className={styles.wrapper}>
      <h1 className={styles.title}>Найдите события, хобби, или компанию единомышленников</h1>
      <div className={styles.section}>
        <p className={styles.paragraph}>
          Выбор на любой вкус - от фестивалей настольных игр до рок-концертов
        </p>
        <div className={styles.slider}>
          <Slider events={events} cardHeight={450} cardWidth={300} />
        </div>
      </div>
    </div>
  );
};
