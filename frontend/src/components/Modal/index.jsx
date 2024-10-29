'use client';
import Register from './register';
import Login from './login';
import Verify from './Registration/verify';
import { useSelector } from 'react-redux';
import { ModalPage } from '@store/slices/modalSlice';
import { useEffect, useState } from 'react';

export default function Modal() {
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);

  const page = useSelector((state) => state.modal.page);

  function selectModal(page) {
    switch (page) {
      case ModalPage.Login:
        return <Login />;
      case ModalPage.Register:
        return <Register />;
      case ModalPage.Verify:
        return <Verify />;
    }
    return <></>;
  }

  return isClient ? selectModal(page) : <></>;
}
