'use client';
import routes from '@routes';
import { authStates, setAuth } from '@store/authSlice';
import { useRouter } from 'next/navigation';
import { useDispatch } from 'react-redux';
import styles from './index.module.css';
import { authed } from '@/services/axiosInstance';

export default () => {
  const router = useRouter();
  const dispatch = useDispatch();

  async function onClick(e) {
    e.preventDefault();
    await authed.get('/logout');
    dispatch(setAuth(authStates.unAuth));
    router.replace(routes.HOME);
    router.refresh();
  }

  return (
    <button className={styles.btn} onClick={onClick}>
      Выйти
    </button>
  );
};
