'use client';
import InputField from './inputField';
import styles from './index.module.css';
import { PageType } from '.';
import { useEffect, useRef, useState } from 'react';

export default function Login({ modal, setModal }) {
  const dialog = useRef();

  useEffect(() => {
    dialog.current?.showModal();
    dialog.current?.addEventListener('close', (event) => {
      setOpen(false);
    });
  }, [modal]);

  function onClickRegister(e) {
    setModal(PageType.Register);
    e.preventDefault();
  }

  function onClickLogin(e) {
    console.log(e.target);
    
    //setModal(PageType.None);
    e.preventDefault();
  }

  return (
    <dialog ref={dialog} className={styles.dialog}>
      <h1 className={styles.title}>Вход</h1>
      <form onSubmit={onClickLogin} className={styles.form}>
        <InputField placeholder="E-mail" name="email" />
        <InputField placeholder="Пароль" name="password" />
        <button type="submit" className={styles.button_submit}>
          Войти
        </button>
      </form>
      <div className={styles.register_wrap}>
        <p className={styles.register_desc}>Нет аккаунта?</p>
        <a className={styles.register_button} href="/" onClick={onClickRegister}>
          Создайте аккаунт
        </a>
      </div>
    </dialog>
  );
}
