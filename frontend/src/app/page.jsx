import Layout from '@/components/Layout';
import Slider from '@/components/Slider';
import mock_img from '@public/mock_img.jpg';
import styles from './page.module.css';

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

const events = [
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img
  }
];
