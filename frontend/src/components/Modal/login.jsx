'use client';
import { unauth_fetch } from '@/utils/fetch';
import { ModalPage, setModal } from '@store/slices/modalSlice';
import { useRouter } from 'next/navigation';
import { useEffect, useRef } from 'react';
import { useDispatch } from 'react-redux';
import InputField from '../InputField';
import styles from './index.module.css';

export default function Login() {
  const dialog = useRef();
  const dispatch = useDispatch();
  const router = useRouter();

  useEffect(() => {
    dialog.current?.showModal();

    function clickEvent(e) {
      let clickInside = dialog.current?.contains(e.target) && e.target !== dialog.current;
      if (!clickInside) dispatch(setModal(ModalPage.None));
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

  async function onSubmit(e) {
    e.preventDefault();
    await unauth_fetch('/api/login', 'post', new FormData(e.target));
    dispatch(setModal(ModalPage.None));
    router.refresh();
  }

  return (
    <dialog ref={dialog} className={styles.dialog}>
      <div className={styles.dialog_wrap}>
        <h1 className={styles.title}>Вход</h1>
        <form onSubmit={onSubmit} className={styles.form}>
          <InputField placeholder="Имя" name="username" />
          <InputField type="password" placeholder="Пароль" name="password" />
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
