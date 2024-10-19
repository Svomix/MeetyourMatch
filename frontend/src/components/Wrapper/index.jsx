import styles from './index.module.css';

export default ({ children }) => {
  return <div className={styles.wrapper}>{children}</div>;
};
