'use client';
import { ModalPage } from '@store/modalSlice/index';
import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import Login from './login';
import Register from './register';
import Verify from './Registration/verify';
import PlaceEditor from './place-editor';

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
      case ModalPage.PlaceEditor:
        return <PlaceEditor/>;
    }
    return <></>;
  }

  return isClient ? selectModal(page) : <></>;
}
