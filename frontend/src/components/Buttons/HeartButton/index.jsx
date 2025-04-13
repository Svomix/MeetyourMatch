'use client';
import classNames from '@/utils/classnames';
import { useState } from 'react';
import styles from './index.module.css';
import { getIsLoggedIn } from '@/services/authService';
import { useDispatch } from 'react-redux';
import { ModalPage, setModal } from '@store/modalSlice/index';

export default ({ width, height, className, onClick }) => {
  const dispatch = useDispatch();
  let is_logged = getIsLoggedIn();
  const [fill, setFill] = useState(false);

  const heart_click = (e) => {
    e.preventDefault();
    if (is_logged) {
      onClick ? onClick() : null;
      setFill((fill) => !fill);
    } else dispatch(setModal(ModalPage.Login));
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
