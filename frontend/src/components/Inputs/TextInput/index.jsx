import styles from './index.module.css';
import classNames from '@/utils/classnames';

export default ({ className, text, warned, ...rest }) => {
  return (
    <textarea
      value={text}
      rows={3}
      className={classNames(styles.text_input, warned && styles.warned, className)}
      {...rest}
    />
  );
};
