'use client';
import { ModalPage, setModal } from '@store/modalSlice/index';
import { useDispatch } from 'react-redux';
import styles from './index.module.css';

export default () => {
  const dispatch = useDispatch();

  return (
    <button onClick={() => dispatch(setModal(ModalPage.Login))} className={styles.btn}>
      Войти
    </button>
  );
};
