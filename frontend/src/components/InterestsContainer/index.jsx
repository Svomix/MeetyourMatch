'use client';
import { authed } from '@/services/axiosInstance';
import { useEffect, useState } from 'react';
import AddInterest from './AddInterest';
import styles from './index.module.css';

export default function InterestsContainer() {
  const [allInterests, setAllInterests] = useState();
  const [myInterests, setMyInterests] = useState();

  const convertData = (data) => data.map((e) => ({ key: e.id, text: e.name }));
  const getAvailableInterests = () =>
    allInterests?.filter((el) => !myInterests?.find((el2) => el.key == el2.key));

  const onRemove = (obj) => {
    authed
      .delete(`/account/interests?id=${obj.key}`)
      .then(() => setMyInterests(myInterests?.filter((el) => el.key != obj.key)));
  };

  const onSelect = (obj) => {
    authed
      .post(`/account/interests?id=${obj.key}`)
      .then(() => setMyInterests((prev) => [...prev, obj]));
  };

  useEffect(() => {
    authed.get('/interests').then((v) => setAllInterests(convertData(v.data)));
    authed.get('/account/interests').then((v) => setMyInterests(convertData(v.data)));
  }, []);

  return (
    <div className={styles.wrapper}>
      <div className={styles.main_container_wrap}>
        <div className={styles.main_container}>
          {myInterests?.map((el) => (
            <div key={el.key} className={styles.interest_item_wrap}>
              <div className={styles.interest_item}>
                {el.text}
                <button className={styles.interest_remove} onClick={() => onRemove(el)}>
                  {cross}
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
      <AddInterest
        data={getAvailableInterests()}
        onSelect={onSelect}
        placeholder={'начните печатать...'}
      />
    </div>
  );
}

const cross = (
  <svg viewBox="5 5 14 14" width={20} height={20}>
    <path d="M6.99486 7.00636C6.60433 7.39689 6.60433 8.03005 6.99486 8.42058L10.58 12.0057L6.99486 15.5909C6.60433 15.9814 6.60433 16.6146 6.99486 17.0051C7.38538 17.3956 8.01855 17.3956 8.40907 17.0051L11.9942 13.4199L15.5794 17.0051C15.9699 17.3956 16.6031 17.3956 16.9936 17.0051C17.3841 16.6146 17.3841 15.9814 16.9936 15.5909L13.4084 12.0057L16.9936 8.42059C17.3841 8.03007 17.3841 7.3969 16.9936 7.00638C16.603 6.61585 15.9699 6.61585 15.5794 7.00638L11.9942 10.5915L8.40907 7.00636C8.01855 6.61584 7.38538 6.61584 6.99486 7.00636Z"></path>
  </svg>
);
