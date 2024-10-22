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
        <Search placeholder='поиск по названию или #тегу'/>
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
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 1
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 2
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 3
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 4
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 5
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 6
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 7
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 8
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 9
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 10
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 11
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 12
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 13
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 14
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 15
  },
  {
    title: 'Lorem, ipsum dolor.',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Iure fugit dicta deserunt necessitatibus unde incidunt modi perferendis. Illo, quis voluptatum.',
    tags: '#Lorem #ipsum #dolor',
    img: mock_img,
    id: 16
  }
];
