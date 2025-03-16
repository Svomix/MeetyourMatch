import classNames from '@/utils/classnames';
import styles from './index.module.css';

export const LeftArrow = ({ width, height, onClick, className, disabled }) => {
  return (
    <svg
      onClick={onClick}
      className={classNames(styles.arrow_btn, disabled && styles.arrow_btn_dis, className)}
      width={width || 20}
      height={height || 20}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
    >
      <path d="M15 5l-7 7 7 7" />
    </svg>
  );
};

export const RightArrow = ({ width, height, onClick, className, disabled }) => {
  return (
    <svg
      onClick={onClick}
      className={classNames(styles.arrow_btn, disabled && styles.arrow_btn_dis, className)}
      width={width || 20}
      height={height || 20}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
    >
      <path d="M9 5l7 7-7 7" />
    </svg>
  );
};
