import styles from './index.module.css';
import classNames from '@/utils/classnames';

export default ({ className, number, warned, ...rest }) => {
  return (
    <input
      type="number"
      value={number}
      className={classNames(className, styles.meta_input, warned && styles.warned)}
      {...rest}
    />
  );
};
