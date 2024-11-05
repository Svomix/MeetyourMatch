import CalendarCell from './Cell';
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
            <CalendarCell
              key={i + 'd'}
              index={i}
              style={i == 1 ? { 'grid-column-start': '3' } : {}}
            />
          );
        })}
      </div>
    </section>
  );
};

const week_days = [
  'понедельник',
  'вторник',
  'среда',
  'четверг',
  'пятница',
  'суббота',
  'воскресенье'
];

const month_start = 2;
const month_length = 30;
