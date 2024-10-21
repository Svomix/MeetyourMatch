'use client';
import styles from './index.module.css';
import Register from './register';
import Login from './login';
import { useSelector } from 'react-redux';
import { ModalPage } from '@store/slices/modalSlice';

export const PageType = ModalPage;

export default function Modal() {
  /*useEffect(() => {
    if (modal == ModalPage.None) return;
    dialog.current?.showModal();
    dialog.current?.addEventListener('close', (event) => {
      setModal(ModalPage.None);
    });
  }, [modal]);*/

  const page = useSelector((state) => state.modal.page);

  function selectModal(page) {
    switch (page) {
      case ModalPage.Login:
        return <Login />;
      case ModalPage.Register:
        return <Register />;
    }
    return <></>;
  }

  return selectModal(page);
}
