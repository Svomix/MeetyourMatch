import Calendar from '@components/Buttons/CalendarButton';
import Heart from '@components/Buttons/HeartButton';
import mock_img from '@public/mock_img.jpg';
import Image from 'next/image';
import Link from 'next/link';
import styles from './index.module.css';

export default ({ path, card }) => {
  return (
    <article className={styles.wrapper}>
      <div className={styles.img_section}>
        <div className={styles.left_side}>
          <h1 className={styles.title}>{card.title}</h1>
          <h2 className={styles.meta}>Дата: {card.date}</h2>
          <h3 className={styles.meta}>Место: {card.place}</h3>
          <h4 className={styles.meta}>Цена: {card.price}₽</h4>
          <div className={styles.actions}>
            <div className={styles.icons}>
              <Heart width={50} height={50} />
              <Calendar width={50} height={50} />
            </div>
            <Link className={styles.link} href={card.from} target="_blank">
              К источнику
            </Link>
          </div>
        </div>
        <div className={styles.right_side}>
          <Image className={styles.img} src={mock_img} />
          <div className={styles.fade_out}></div>
        </div>
      </div>
      <div className={styles.extra_info}>
        <p className={styles.description}>
          Описание:
          <br />
          {card.description}
        </p>
        <p className={styles.tags}>{card.tags}</p>
      </div>
    </article>
  );
};
