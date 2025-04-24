'use client';
import classNames from '@/utils/classnames';
import styles from './index.module.css';

export default ({ width, height, className, active, onClick, disabled }) => {
  return (
    <svg
      onClick={onClick}
      className={classNames(
        styles.icon,
        className,
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
      <rect x="3" y="4" width="18" height="16" rx="2" ry="2" />
      <line x1="16" y1="2" x2="16" y2="6" />
      <line x1="8" y1="2" x2="8" y2="6" />
      <line x1="3" y1="10" x2="21" y2="10" />
    </svg>
  );
};
