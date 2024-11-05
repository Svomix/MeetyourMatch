'use client';
import styles from './index.module.css';

const mock_test = [
  "Java", "Rust", "C++", "Python", "Lua", "Javascript", "C#", "HTML"
]

export default function InterestsContainer() {
  return (
    <div className={styles.main_container_wrap}>
      <div className={styles.main_container}>
          {mock_test.map((el, index) => (
            <div key={index} className={styles.interest_item_wrap}>
            <div key={index} className={styles.interest_item}>{el}</div>
          </div>
          ))}
      </div>
    </div>
  );
}
