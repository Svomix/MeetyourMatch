'use client';
import Modal from '@components/Modal';
import styles from './index.module.css';
import { useDispatch } from 'react-redux';
import { setModal, ModalPage } from '@store/slices/modalSlice';

export default () => {
  const dispatch = useDispatch();

  return (
    <>
      <div>
        <Modal />
      </div>
      <button onClick={() => dispatch(setModal(ModalPage.Login))} className={styles.btn}>
        Войти
      </button>
    </>
  );
};
