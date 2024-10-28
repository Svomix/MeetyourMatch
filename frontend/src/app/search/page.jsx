import Layout from '@/components/Layout';
import Card from '@components/Card';
import CardNavigation from '@components/CardNavigation';
import Search from '@components/Search';
import mock_img from '@public/mock_img.jpg';
import styles from './page.module.css';

export default () => {
  return (
    <Layout>
      <div className={styles.wrapper}>
        <Search placeholder="поиск по названию или #тегу" />
        <div className={styles.events}>
          {mock_cards.map((el, index) => (
            <Card key={Math.random() * index} event={el}></Card>
          ))}
        </div>
        <div className={styles.pages}>
          <CardNavigation />
        </div>
      </div>
    </Layout>
  );
};

const mock_cards = [
  {
    title: 'Lorem, ipsum dolor. Lorem, ipsum dolor. Lorem, ipsum dolor. Lorem, ipsum dolor.',
    date: '17:00 01.01.2020 17:00 01.01.2020 17:00 01.01.2020 ',
    tags: '#Lorem #ipsum #dolor #Lorem #ipsum #dolor #Lorem #ipsum #dolor',
    img: mock_img,
    id: 1
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2020',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 2
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2020',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 3
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2020',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 4
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2020',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 5
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2020',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 6
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2020',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 7
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2020',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 8
  }
];
