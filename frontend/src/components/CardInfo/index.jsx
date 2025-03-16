import Calendar from '@components/Buttons/CalendarButton';
import Heart from '@components/Buttons/HeartButton';
import mock_event_img from '@public/mock_event_img.jpg';
import Image from 'next/image';
import Link from 'next/link';
import styles from './index.module.css';
import CommentBox from '@components/CommentBox';
import { comment } from 'postcss';

export default ({ path, event }) => {
  return (
    <>
      {event && (
        <article className={styles.wrapper}>
          <div className={styles.img_section}>
            <div className={styles.left_side}>
              <h1 className={styles.title}>{event.title}</h1>
              <h2 className={styles.meta}>Дата: {new Date(event.date).toLocaleString('ru-RU')}</h2>
              <h3 className={styles.meta}>Место: {event.place}</h3>
              <h4 className={styles.meta}>Цена: {event.price}</h4>
              <div className={styles.actions}>
                <div className={styles.icons}>
                  <Heart width={50} height={50} />
                  <Calendar width={50} height={50} />
                </div>
                <Link className={styles.link} href={event.sourceUrl} target="_blank">
                  К источнику
                </Link>
              </div>
            </div>
            <div className={styles.right_side}>
              <Image
                alt={'event image'}
                className={styles.img}
                src={event.coverImgUrl || mock_event_img}
                width={2000}
                height={2000}
              />
              <div className={styles.fade_out}></div>
            </div>
          </div>
          <div className={styles.extra_info}>
            <h4 className={styles.desc_name}>Описание:</h4>
            <p className={styles.description}>{event.description}</p>
            <p className={styles.tags}>#отдых #искусство</p>
          </div>
          <div className={styles.comments}>
            <CommentBox comments={comments} />
          </div>
        </article>
      )}
    </>
  );
};

let comments = [
  {
    comment_id: 1,
    author: 'Goy Goyev',
    text: 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Quod vero provident corrupti ab voluptates blanditiis nesciunt. Sint minus quasi reiciendis!'
  },
  {
    comment_id: 2,
    author: 'Goy Goyev',
    text: 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Quod vero provident corrupti ab voluptates blanditiis nesciunt. Sint minus quasi reiciendis!'
  },
  {
    comment_id: 3,
    author: 'Goy Goyev',
    text: 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Quod vero provident corrupti ab voluptates blanditiis nesciunt. Sint minus quasi reiciendis!'
  }
];
