import styles from './index.module.css';
import Navbar from './Navbar';
import UserBar from './UserBar';

export default async () => {
  return (
    <header className={styles.header}>
      <Navbar />
      <UserBar />
    </header>
  );
};
