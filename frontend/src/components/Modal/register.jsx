'use client';

import { useEffect, useRef, useState } from 'react';
import InputField from './inputField';
import styles from './index.module.css';
import { PageType } from './index';

export default function Register({ modal, setModal }) {
  const dialog = useRef();

  useEffect(() => {
    dialog.current?.showModal();
    dialog.current?.addEventListener('close', (event) => {
      setOpen(false);
    });
  }, [modal]);

  function onClickRegister(e) {
    console.log(e);
    setModal(PageType.None);
    e.preventDefault();
    return false;
  }

  function onClickLogin(e) {
    setModal(PageType.Login);
    e.preventDefault();
  }

  return open ? (
    <dialog ref={dialog} className={styles.dialog}>
      <h1 className={styles.title}>Регистрация</h1>
      <form onSubmit={onClickRegister} className={styles.form}>
        <InputField placeholder="Имя" name="name" />
        <InputField placeholder="E-mail" name="email" />
        <InputField placeholder="Пароль" name="password" />
        <InputField placeholder="Повторите пароль" name="password2" />
        <input type="checkbox"></input>
        <button type="submit" className={styles.button_submit}>
          Зарегистрироваться
        </button>
      </form>
      <div className={styles.register_wrap}>
        <p className={styles.register_desc}>Уже есть аккаунт?</p>
        <a className={styles.register_button} href="/" onClick={onClickLogin}>
          Войти
        </a>
      </div>
    </dialog>
  ) : (
    <></>
  );
}
