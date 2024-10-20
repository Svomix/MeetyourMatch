'use client';
import { useEffect, useRef, useState } from 'react';
import styles from './index.module.css';

import Register from './register';
import Login from './login';

const ModalPage = {
  None: 'None',
  Login: 'Login',
  Register: 'Register'
};

export const PageType = ModalPage;

export default function Modal({ modal, setModal }) {

  /*useEffect(() => {
    if (modal == ModalPage.None) return;
    dialog.current?.showModal();
    dialog.current?.addEventListener('close', (event) => {
      setModal(ModalPage.None);
    });
  }, [modal]);*/

  function selectModal(modal, setModal) {
    switch (modal) {
      case ModalPage.Login:
        return <Login modal={modal} setModal={setModal} />
      case ModalPage.Register:
        return <Register modal={modal} setModal={setModal} />
    }
    return <></>;
  }

  return selectModal(modal, setModal);
}
