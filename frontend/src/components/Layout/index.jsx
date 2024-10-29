import Modal from '@components/Modal';
import Footer from '../Footer';
import Header from '../Header';
import styles from './index.module.css';

export default ({ children }) => {
  return (
    <>
      <Modal />
      <div className={styles.window}>
        <div className={styles.wrapper}>
          <Header />
          <div className={styles.container}>{children}</div>
          <Footer />
        </div>
      </div>
    </>
  );
};
