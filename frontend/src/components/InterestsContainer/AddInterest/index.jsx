'use client';
import { useState } from 'react';
import InterestsDropdown from '../InterestsDropdown';
import classNames from '@/utils/classnames';
import styles from './index.module.css';

export default ({ data, placeholder, onSelect, className }) => {
  const [drop, setDrop] = useState(false);

  const onAdd = (data) => {
    setDrop(false);
    onSelect(data);
  };
  const onClick = (e) => {
    e.preventDefault();
    setDrop((prev) => !prev);
  };

  return (
    <div className={styles.wrapper}>
      {!drop && (
        <button className={classNames(styles.btn, className)} onClick={onClick}>
          + Добавить
        </button>
      )}
      {drop && (
        <InterestsDropdown
          data={data}
          placeholder={placeholder}
          onSelect={onAdd}
          className={styles.drop}
        />
      )}
    </div>
  );
};
