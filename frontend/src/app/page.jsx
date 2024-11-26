import mock_img from '@public/mock_img.jpg';
import styles from './page.module.css';

export default () => {
  return (
    <>
      <h1 className={styles.title}>Найдите события, хобби, или компанию единомышленников</h1>
      <div className={styles.wrapper}>
        <p className={styles.paragraph}>
          Выбор на любой вкус - от фестивалей настольных игр до рок-концертов
        </p>
        <div className={styles.slider}></div>
      </div>
    </>
  );
};

const events = [
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 1
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 2
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 3
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 4
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 5
  }
];
