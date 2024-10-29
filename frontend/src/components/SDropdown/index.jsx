'use client';
import { useRef, useState } from 'react';
import styles from './index.module.css';
import classNames from '@/utils/classnames';

export default function SDropdown({ data, placeholder, onSelect, className }) {
  let [filter, setFilter] = useState('');

  const input = useRef();
  const data_wrap = useRef();

  function onFocus() {
    data_wrap.current?.classList.add(styles.block);
    data_wrap.current?.classList.remove(styles.invisible);
    input.current.placeholder = '';
    setFilter('');
  }

  function onChange(e) {
    setFilter(e.target.value);
  }

  function getFiltered(data) {
    return data.filter(
      (row) => row.text.toLocaleLowerCase().indexOf(filter.toLocaleLowerCase()) !== -1
    );
  }

  function onBlur() {
    data_wrap.current?.classList.add(styles.invisible);
    data_wrap.current?.classList.remove(styles.block);
    input.current.placeholder = placeholder;
    if (!data.some((row) => row.text == input.current.value)) input.current.value = '';
    //if (filter !== '') input.current.value = '';
  }

  function onClick(e) {
    input.current.value = e.text;
    //setTimeout(() => setFilter(''), 100);
  }

  return (
    <div className={styles.dropdown_wrap}>
      <div className={classNames(styles.dropdown, className)}>
        <button className={styles.input_wrap} onClick={() => input.current?.focus()}>
          <input
            placeholder={placeholder}
            ref={input}
            className={styles.input}
            type="text"
            onFocus={onFocus}
            onBlur={onBlur}
            onChange={onChange}
          ></input>
        </button>
        <span ref={data_wrap} className={classNames(styles.data_container, styles.invisible)}>
          {getFiltered(data).map((row) => (
            <button onMouseDown={() => onClick(row)} key={row.key} className={styles.data}>
              {row.text}
            </button>
          ))}
        </span>
      </div>
    </div>
  );
}
