'use client';
import styles from './index.module.css';
import classNames from '@/utils/classnames';

export default ({ className, date, warned, ...rest }) => {
  return (
    <input
      type="date"
      value={date}
      className={classNames(className, styles.meta_input, warned && styles.warned)}
      {...rest}
    />
  );
};
