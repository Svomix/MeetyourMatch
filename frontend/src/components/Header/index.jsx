import Navbar from './Navbar';
import Signbar from './Signbar';
import styles from './index.module.css';

export default () => {
  return (
    <header className={styles.header}>
      <Navbar></Navbar>
      <Signbar></Signbar>
    </header>
  );
};
