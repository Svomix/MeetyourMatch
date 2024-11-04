'use client';
import base_styles from '../index.module.css';
import styles from './verify.module.css';
import { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';
import { setModal, ModalPage } from '@store/slices/modalSlice';
import Image from 'next/image';
import svg2 from '@public/check-circle-svgrepo-com.svg';

export default function Verify() {
  const dialog = useRef();
  const dispatch = useDispatch();

  useEffect(() => {
    dialog.current?.showModal();

    function clickEvent(e) {
      let clickInside = dialog.current?.contains(e.target) && e.target !== dialog.current;

      if (!clickInside) {
        dispatch(setModal(ModalPage.None));
      }
    }

    document.addEventListener('click', clickEvent);

    return () => {
      document.removeEventListener('click', clickEvent);
    };
  }, []);

  return open ? (
    <dialog ref={dialog} className={base_styles.dialog}>
      <div className={base_styles.dialog_wrap}>
        <h1 className={base_styles.title}>Оплата</h1>
        <h2 className={styles.desc}>Приложите карту к экрану</h2>
        <Image className={styles.image} alt="verify" src={svg2}></Image>
      </div>
    </dialog>
  ) : (
    <></>
  );
}
