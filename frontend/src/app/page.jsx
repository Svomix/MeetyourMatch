'use client';
import classNames from '@/utils/classnames';
import DraggableCard from '@components/DraggableCard';
import mock_img from '@public/mock_img.jpg';
import { useState } from 'react';
import styles from './page.module.css';

export default () => {
  const [active, setActive] = useState();

  return (
    <div className={styles.wrapper}>
      <div className={classNames(styles.reject, active == 1 && styles.active)}>Пропустить</div>
      <div className={classNames(styles.accept, active == 2 && styles.active)}>Нравится</div>
      <div className={styles.text}>
      <h1 className={styles.title}>Рекомендуемое</h1>
      <p className={styles.desc}>Попробуйте перетащить карту</p>
      </div>
      <DraggableCard setActive={setActive} event={events[0]} />
    </div>
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
