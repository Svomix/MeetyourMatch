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

        {[...Array(month_length)].map((_, i) => (
          <Cell
            key={i + 'd'}
            index={i}
            event_list={mock}
            style={i++ == 1 ? { gridColumnStart: month_start } : {}}
          />
        ))}
      </div>
    </section>
  );
};

const mock = [
  'событие',
  'праздник',
  'день рождения',
  'корпоратив',
  'отдых',
  'выходной',
  'сессия',
  'Lorem ipsum dolor sit amet consectetur adipisicing elit. Voluptate, consectetur! Eum omnis, neque facilis veniam sit nam ex tenetur eligendi.',
  'событие',
  'праздник',
  'день рождения',
  'корпоратив',
  'отдых',
  'выходной',
  'сессия'
];

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
