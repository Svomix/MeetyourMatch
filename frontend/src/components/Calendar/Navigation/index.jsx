import { LeftArrow, RightArrow } from '@components/Buttons/ArrowButtons';
import styles from './index.module.css';

export default ({ setYear, setMonth, year, month }) => {
  const subMonth = () => {
    if (month == 0) setYear(year - 1);
    setMonth(((month - 1 + 12) % 12) % 12);
  };

  const addMonth = () => {
    if (month == 11) setYear(year + 1);
    setMonth((month + 1) % 12);
  };

  return (
    <div className={styles.wrapper}>
      <LeftArrow width={48} height={48} onClick={subMonth} />
      <div className={styles.info_wrapper}>
        <h1 className={styles.info}>{months[month]}</h1>
        <h1 className={styles.info}>{year}г.</h1>
      </div>
      <RightArrow width={48} height={48} onClick={addMonth} />
    </div>
  );
};

let months = {
  0: 'Январь',
  1: 'Февраль',
  2: 'Март',
  3: 'Апрель',
  4: 'Май',
  5: 'Июнь',
  6: 'Июль',
  7: 'Август',
  8: 'Сентябрь',
  9: 'Октябрь',
  10: 'Ноябрь',
  11: 'Декабрь'
};
