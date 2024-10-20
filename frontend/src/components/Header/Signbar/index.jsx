'use client';
import Modal, {PageType} from '@/components/Modal';
import styles from './index.module.css';
import { useState } from 'react';

export default () => {
  const [modal, setModal] = useState(PageType.None);

  return (
    <>
      <div>
        <Modal modal={modal} setModal={setModal} />
      </div>
      <button onClick={() => setModal(PageType.Login)} className={styles.btn}>
        Войти
      </button>
    </>
  );
};
