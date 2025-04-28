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
      viewBox="2 2 21 21"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path
        d="M4.42602 12.3115L12 19.8854L19.574 12.3115C21.4753 10.4101 21.4753 7.32738 19.574 5.42602C17.6726 3.52466 14.5899 3.52466 12.6885 5.42602L12 6.11456L11.3115 5.42602C9.4101 3.52466 6.32738 3.52466 4.42602 5.42602C2.52466 7.32738 2.52466 10.4101 4.42602 12.3115Z"
        strokeWidth="2"
        strokeLinejoin="round"
      />
      <path
        d="M12 6L10 9.99995L14 11L12 14"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
};
