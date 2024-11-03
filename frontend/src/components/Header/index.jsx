import { cookies } from 'next/headers';
import Navbar from './Navbar';
import styles from './index.module.css';
import Singedbar from './Singedbar';
import Unsignedbar from './Unsignedbar';
import { tokenType } from '@/services/authService';

export default async () => {
  const cookieStore = await cookies();

  const token = cookieStore.get(tokenType.ACCESS_TOKEN)

  return (
    <header className={styles.header}>
      <Navbar/>
      {token ? <Singedbar/> : <Unsignedbar/>}
    </header>
  );
};
