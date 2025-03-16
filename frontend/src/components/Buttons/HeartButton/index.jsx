'use client';
import classNames from '@/utils/classnames';
import { useState } from 'react';
import styles from './index.module.css';

export default ({ width, height, className }) => {
  const [fill, setFill] = useState(false);

  const heart_click = (e) => {
    setFill((fill) => !fill);
    e.preventDefault();
  };

  return (
    <svg
      onClick={heart_click}
      className={classNames(className, styles.icon, fill && styles.active)}
      width={width || 20}
      height={height || 20}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21l7.8-7.6 1-1a5.5 5.5 0 0 0 0-7.8z" />
    </svg>
  );
};
