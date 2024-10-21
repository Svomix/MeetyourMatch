'use client';
import InputField from './inputField';
import styles from './index.module.css';
import { useEffect, useRef } from 'react';
import { useDispatch } from 'react-redux';
import { setModal, ModalPage } from '@store/slices/modalSlice';

export default function Login() {
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

  function onClickRegister(e) {
    dispatch(setModal(ModalPage.Register));
    e.preventDefault();
  }

  function onClickLogin(e) {
    console.log(e.target);

    dispatch(setModal(ModalPage.None));
    e.preventDefault();
  }

  return (
    <dialog ref={dialog} className={styles.dialog}>
      <div className={styles.dialog_wrap}>
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
      </div>
    </dialog>
  );
}
