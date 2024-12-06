'use client';
import { authed } from '@/services/axiosInstance';
import routes from '@routes';
import { setAuth } from '@store/authSlice';
import { useRouter } from 'next/navigation';
import { useDispatch } from 'react-redux';
import styles from './index.module.css';

export default () => {
  const router = useRouter();
  const dispatch = useDispatch();

  async function onClick(e) {
    e.preventDefault();
    await authed.get('/logout');
    dispatch(setAuth(false));
    router.replace(routes.HOME);
    router.refresh();
  }

  return (
    <button className={styles.btn} onClick={onClick}>
      Выйти
    </button>
  );
};
