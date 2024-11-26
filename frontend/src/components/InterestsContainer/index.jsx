'use client';
import { useState } from 'react';
import AddInterest from './AddInterest';
import styles from './index.module.css';

const mock_test = [
  { key: 'java', text: 'Java' },
  { key: 'rust', text: 'Rust' },
  { key: 'cpp', text: 'C++' },
  { key: 'python', text: 'Python' },
  { key: 'lua', text: 'Lua' },
  { key: 'js', text: 'Javascript' },
  { key: 'csh', text: 'C#' },
  { key: 'html', text: 'HTML' },
  { key: 'css', text: 'CSS' }
];

export default function InterestsContainer() {
  const [data, setData] = useState(mock_test);
  const onSelect = (obj) => {
    setData((prev) => [...prev, obj]);
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.main_container_wrap}>
        <div className={styles.main_container}>
          {data.map((el, index) => (
            <div key={index} className={styles.interest_item_wrap}>
              <div key={index} className={styles.interest_item}>
                {el.text}
              </div>
            </div>
          ))}
        </div>
      </div>
      <AddInterest data={data} onSelect={onSelect} placeholder={'начните печатать...'} />
    </div>
  );
}
