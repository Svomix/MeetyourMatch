import { tokenType } from '@/services/authService';
import { cookies } from 'next/headers';
import styles from './index.module.css';
import Navbar from './Navbar';
import Singedbar from './Singedbar';
import Unsignedbar from './Unsignedbar';

export default async () => {
  const cookieStore = await cookies();
  const token = cookieStore.get(tokenType.ACCESS_TOKEN);

  return (
    <header className={styles.header}>
      <Navbar />
      {token?.value ? <Singedbar /> : <Unsignedbar />}
    </header>
  );
};
