'use client';
import Cell from './Cell';
import Navigation from './Navigation';
import styles from './index.module.css';
import { useEffect, useState } from 'react';
import { authed } from '@/services/axiosInstance';

const daysOfWeek = [
  'Понедельник',
  'Вторник',
  'Среда',
  'Четверг',
  'Пятница',
  'Суббота',
  'Воскресенье'
];

function getDaysInMonth(month, year) {
  return new Date(year, month + 1, 0).getDate();
}

function getFirstDayOfMonth(month, year) {
  const day = new Date(year, month, 1).getDay();
  return day === 0 ? 6 : day - 1; // 0 - пн, 6 - вс
}

export default () => {
  const today = new Date();
  const [events, setEvents] = useState();
  const [year, setYear] = useState(today.getFullYear()); // Текущий год
  const [month, setMonth] = useState(today.getMonth()); // Текущий месяц (0 - январь, 11 - декабрь)

  useEffect(() => {
    authed.get(`/account/events/calendar`).then((resp) => {
      setEvents(resp.data);
    });
  }, []);

  return (
    <section className={styles.wrapper}>
      <Navigation setYear={setYear} setMonth={setMonth} year={year} month={month} />
      <div className={styles.days}>
        {daysOfWeek.map((day) => (
          <p key={day.slice(0, 2)} className={styles.week_days}>
            {day}
          </p>
        ))}

        {[...Array(getDaysInMonth(month, year))].map((_, i) => (
          <Cell
            key={i + 'd'}
            index={i + 1}
            event_list={events?.filter((event) => {
              const date = new Date(event.event.date);
              return (
                date.getFullYear() === year && date.getMonth() === month && date.getDate() === i
              );
            })}
            style={i++ == 0 ? { gridColumnStart: getFirstDayOfMonth(month, year) + 1 } : {}}
          />
        ))}
      </div>
    </section>
  );
};
