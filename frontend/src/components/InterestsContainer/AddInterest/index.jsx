'use client';
import { useRef, useState } from 'react';
import styles from './index.module.css';
import classNames from '@/utils/classnames';

export default ({ data, onSelect, className }) => {
  const [drop, setDrop] = useState(false);
  const [filter, setFilter] = useState('');

  const input = useRef();

  function getFiltered(data) {
    return data?.filter(
      (row) => row.text.toLocaleLowerCase().indexOf(filter.toLocaleLowerCase()) !== -1
    );
  }

  function onFocus(e) {
    e.preventDefault();
    setDrop(true);
    input.current.placeholder = 'Начните печатать...';
    setFilter('');
  }

  function onChange(e) {
    e.preventDefault();
    setFilter(e.target.value);
  }

  function onBlur(e) {
    e.preventDefault();
    setDrop(false);
    input.current.placeholder = '+ Добавить';
    if (!data.some((row) => row.text == input.current.value)) input.current.value = '';
  }

  function onAddInterest(e) {
    setDrop(false);
    input.current?.blur();
    onSelect({ key: e.key, text: e.text });
  }
  console.log(getFiltered(data));
  console.log(data?.length);
  return (
    <div className={styles.wrapper}>
      <input
        placeholder={'+ Добавить'}
        ref={input}
        className={classNames(!drop && styles.input_btn, drop && styles.input)}
        type="text"
        onFocus={onFocus}
        onBlur={onBlur}
        onChange={onChange}
      />

      <div className={classNames(styles.data_container, !drop && styles.invisible)}>
        {data && getFiltered(data)?.length != 0 ? (
          getFiltered(data).map((row) => (
            <button
              onMouseDown={(e) => {
                e.preventDefault();
                onAddInterest(row);
              }}
              key={row.key}
              className={styles.data_btn}
            >
              {row.text}
            </button>
          ))
        ) : (
          <div className={styles.empty}>Пусто</div>
        )}
      </div>
    </div>
  );
};
