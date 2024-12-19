'use client';
import { authed } from '@/services/axiosInstance';
import { useEffect, useState } from 'react';
import AddInterest from './AddInterest';
import styles from './index.module.css';

export default function InterestsContainer() {
  const [data, setData] = useState();

  const onSelect = (obj) => {
    setData((prev) => [...prev, obj]);
  };

  useEffect(() => {
    authed('/api/interests').then((resp) => console.log(resp));
  }, []);

  return (
    <div className={styles.wrapper}>
      <div className={styles.main_container_wrap}>
        <div className={styles.main_container}></div>
      </div>
      <AddInterest data={data} onSelect={onSelect} placeholder={'начните печатать...'} />
    </div>
  );
}
