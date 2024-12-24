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
    instance.get(`/v1/events?limit=8&page=5`).then((response) => {
      setEvents(response.data.content);
    });
  }, []);

  return (
    <div className={styles.wrapper}>
      <div className={styles.text}>
        <h1 className={styles.title}>Рекомендации</h1>
        <p className={styles.paragraph}>Возможно, Вам будет интересно</p>
      </div>

      <div className={styles.slider}>
        <Slider events={events} cardHeight={450} cardWidth={300} />
      </div>
    </div>
  );
};
