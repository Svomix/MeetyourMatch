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

  const calendar_click = (e) => {
    e.preventDefault();
    if (is_logged) {
      onClick ? onClick() : null;
      setFill((fill) => !fill);
    } else dispatch(setModal(ModalPage.Login));
  };

  return (
    <svg
      onClick={calendar_click}
      className={classNames(styles.icon, className, fill && styles.active)}
      width={width || 20}
      height={height || 20}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <rect x="3" y="4" width="18" height="16" rx="2" ry="2" />
      <line x1="16" y1="2" x2="16" y2="6" />
      <line x1="8" y1="2" x2="8" y2="6" />
      <line x1="3" y1="10" x2="21" y2="10" />
    </svg>
  );
};
