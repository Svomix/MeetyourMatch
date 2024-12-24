'use client';
import { tokenType } from '@/services/authService';
import { authed, unauthed } from '@/services/axiosInstance';
import Slider from '@components/Slider';
import Cookies from 'js-cookie';
import { useEffect, useState } from 'react';
import styles from './index.module.css';

export default () => {
  let [events, setEvents] = useState([]);

  useEffect(() => {
    const instance = Cookies.get(tokenType.ACCESS_TOKEN) ? authed : unauthed;
    instance.get(`/v1/events?limit=8&page=7`).then((response) => {
      setEvents(response.data.content);
    });
  }, []);
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
