import classNames from '@/utils/classnames';
import styles from './index.module.css';

export default function InputField({ name, title, className, ...rest }) {
  return (
    <div className={classNames(styles.container, className)}>
      {/* <p>{title}</p> */}
      <div className={styles.wrap}>
        <input name={name} {...rest} className={styles.input} />
      </div>
    </div>
  );
}
