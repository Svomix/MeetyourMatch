'use client';
import { auth_fetch } from '@/utils/fetch';
import routes from '@routes';
import { useRouter } from 'next/navigation';
import styles from './index.module.css';

export default () => {
  const router = useRouter();

  async function onClick(e) {
    e.preventDefault();
    await auth_fetch('/api/logout');
    router.replace(routes.HOME);
    router.refresh();
  }

  return (
    <button className={styles.btn} onClick={onClick}>
      Выйти
    </button>
  );
};
