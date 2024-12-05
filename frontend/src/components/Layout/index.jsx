import Footer from './Footer';
import Header from './Header';
import styles from './index.module.css';
import Modal from './Modal';

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
