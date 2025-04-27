'use client';
import classNames from '@/utils/classnames';
import { useRef, useState } from 'react';
import styles from './index.module.css';

export default function interestsDropdown({ data, placeholder, onSelect, className }) {
  let [filter, setFilter] = useState('');

  const input = useRef();
  const data_wrap = useRef();

  function onFocus(e) {
    e.preventDefault();
    data_wrap.current?.classList.add(styles.block);
    data_wrap.current?.classList.remove(styles.invisible);
    setFilter('');
  }

  function onChange(e) {
    e.preventDefault();
    setFilter(e.target.value);
  }

  function getFiltered(data) {
    return data.filter(
      (row) => row.text.toLocaleLowerCase().indexOf(filter.toLocaleLowerCase()) !== -1
    );
  }

  function onBlur(e) {
    e.preventDefault();
    data_wrap.current?.classList.add(styles.invisible);
    data_wrap.current?.classList.remove(styles.block);
    input.current.placeholder = placeholder;
    if (!data.some((row) => row.text == input.current.value)) input.current.value = '';
  }

  function onClick(e) {
    input.current.value = e.text;
    onSelect({ key: e.key, text: e.text });
  }

  return (
    <div className={classNames(styles.dropdown_wrap, className)}>
      <input
        placeholder={placeholder}
        ref={input}
        className={styles.input}
        type="text"
        onFocus={onFocus}
        onBlur={onBlur}
        onChange={onChange}
      />

      <span ref={data_wrap} className={classNames(styles.data_container, styles.invisible)}>
        {getFiltered(data).map((row) => (
          <button
            onMouseDown={(e) => {
              e.preventDefault();
              onClick(row);
            }}
            key={row.key}
            className={styles.data}
          >
            {row.text}
          </button>
        ))}
      </span>
    </div>
  );
}
