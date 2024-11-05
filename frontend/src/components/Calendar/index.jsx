import Cell from './Cell';
import Navigation from './Navigation';
import styles from './index.module.css';

export default () => {
  return (
    <section className={styles.wrapper}>
      <Navigation />
      <div className={styles.days}>
        {week_days.map((day) => (
          <p key={day.slice(0, 2)} className={styles.week_days}>
            {day}
          </p>
        ))}
        {[...Array(month_length)].map((_, i) => {
          i++;
          return (
            <Cell key={i + 'd'} index={i} style={i == 1 ? { 'grid-column-start': '3' } : {}} />
          );
        })}
      </div>
    </section>
  );
};

const week_days = [
  'Понедельник',
  'Вторник',
  'Среда',
  'Четверг',
  'Пятница',
  'Суббота',
  'Воскресенье'
];

const month_start = 2;
const month_length = 30;
