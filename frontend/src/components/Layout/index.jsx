import Footer from '../Footer';
import Header from '../Header';
import styles from './index.module.css';

export default ({ children }) => {
  return (
    <div className={styles.window}>
      <div className={styles.wrapper}>
        <Header />
        <container className={styles.container}>{children}</container>
        <Footer />
      </div>
    </div>
  );
};
