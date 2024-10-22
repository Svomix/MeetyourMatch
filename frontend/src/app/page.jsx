import Layout from '@/components/Layout';
import Slider from '@/components/Slider';
import styles from './page.module.css';

const events = [1, 2, 3, 4, 5];

export default () => {
  return (
    <Layout>
      <h1 className={styles.title}>Найдите события, хобби, или компанию единомышленников</h1>
      <div className={styles.wrapper}>
        <p className={styles.paragraph}>
          Выбор на любой вкус - от фестивалей настольных игр до рок-концертов
        </p>
        <div className={styles.slider}>
          <Slider rec_events={events} />
        </div>
      </div>
    </Layout>
  );
};
