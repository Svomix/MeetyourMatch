'use client';
import styles from './index.module.css';
import classNames from '@/utils/classnames';

export default ({ className, label, warned, ...rest }) => {
  return (
    <input
      type="text"
      value={label}
      className={classNames(className, styles.meta_input, warned && styles.warned)}
      {...rest}
    />
  );
};
