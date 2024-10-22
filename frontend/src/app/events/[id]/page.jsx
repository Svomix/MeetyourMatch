'use client';

import Heart from '@components/Heart';
import Layout from '@components/Layout';
import calendar from '@public/calendar.svg';
import mock_img from '@public/mock_img.jpg';
import Image from 'next/image';
import { usePathname } from 'next/navigation';
import styles from './page.module.css';

export default function ExampleClientComponent() {
  const pathname = usePathname().split('/').pop();

  return (
    <Layout>
      <div className={styles.wrapper}>
        <div className={styles.img_block}>
          <div className={styles.img_wrapper}>
            <Image className={styles.img} src={card.img} />
          </div>
          <div className={styles.ui_options}>
            <div className={styles.heart_ui}>
              <Heart width={48} height={48} />
              <div className={styles.heart_count}>52</div>
            </div>
            <Image className={styles.icon} src={calendar} alt="people" />
          </div>
          <div className={styles.tags}>{card.tags}</div>
        </div>
        <div className={styles.text_block}>
          <div className={styles.break}>
            <div className={styles.title}>{'Page: ' + pathname + '. ' + card.title}</div>
            <div className={styles.description}>{card.description}</div>
          </div>
          <div className={styles.break}>
            <div className={styles.info}>{card.date}</div>
            <div className={styles.info}>{card.place}</div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

const card = {
  title: 'Lorem ipsum dolor sit amet consectetur adipisicing elit. ',
  description:
    'Lorem ipsum dolor sit, amet consectetur adipisicing elit. Ut nam unde aspernatur numquam maxime ullam iste recusandae at repudiandae, labore repellendus soluta reiciendis? Exercitationem neque beatae asperiores voluptate corporis aperiam! Lorem ipsum dolor, sit amet consectetur adipisicing elit. A corporis voluptatem ad id quisquam perspiciatis?Lorem ipsum dolor sit, amet consectetur adipisicing elit. Ut nam unde aspernatur numquam maxime ullam iste recusandae at repudiandae, labore repellendus soluta reiciendis?',
  date: '01.01.1970',
  place: 'Lorem ipsum dolor sit.',
  tags: '#Lorem #ipsum #dolor #sit',
  img: mock_img,
  heart_count: 52
};
