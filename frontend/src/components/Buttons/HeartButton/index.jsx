'use client';
import classNames from '@/utils/classnames';
import styles from './index.module.css';

export default ({ width, height, className, active, onClick, disabled }) => {
  return (
    <svg
      onClick={onClick}
      className={classNames(
        className,
        styles.icon,
        disabled ? styles.disabled : active && styles.active
      )}
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
