'use client';
import { tokenType } from '@/services/authService';
import { authed, unauthed } from '@/services/axiosInstance';
import Card from '@components/Card';
import Slider from '@components/Slider';
import Cookies from 'js-cookie';
import { useEffect, useState } from 'react';
import styles from './index.module.css';

export default () => {
  let [events, setEvents] = useState([]);
  let [myEvents, setMyEvents] = useState([]);

  useEffect(() => {
    const instance = Cookies.get(tokenType.ACCESS_TOKEN) ? authed : unauthed;
    instance.get(`/v1/events/recWeb`).then((response) => {
      setEvents(response.data.content);
    });
    instance.get(`/v1/events/my?limit=99`).then((response) => {
      setMyEvents(response.data.content);
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
      {myEvents.length != 0 && (
        <>
          <div className={styles.text}>
            <h1 className={styles.title}>Созданные вами события</h1>
          </div>

          <div className={styles.myEvents}>
            {myEvents?.map((el, index) => (
              <Card key={el.id} event={el} height={450} width={300} />
            ))}
          </div>
        </>
      )}
    </div>
  );
};
