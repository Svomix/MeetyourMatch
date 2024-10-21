'use client';
import InputField from './inputField';
import styles from './index.module.css';
import { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';
import { setModal, ModalPage } from '@store/slices/modalSlice';
import Link from 'next/link';
import routes from '@routes';

export default function Register() {
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
    console.log(e);
    dispatch(setModal(ModalPage.None));
    e.preventDefault();
    return false;
  }

  function onClickLogin(e) {
    dispatch(setModal(ModalPage.Login));
    e.preventDefault();
  }

  const [disabled, setDisabled] = useState(true);

  function onChangeCheckbox(e) {
    setDisabled(!e.target.checked);
  }

  return open ? (
    <dialog ref={dialog} className={styles.dialog}>
      <div className={styles.dialog_wrap}>
        <h1 className={styles.title}>Регистрация</h1>
        <form onSubmit={onClickRegister} className={styles.form}>
          <InputField placeholder="Имя" name="name" />
          <InputField placeholder="E-mail" name="email" />
          <InputField placeholder="Пароль" name="password" />
          <InputField placeholder="Повторите пароль" name="password2" />
          <div className={styles.license_wrap}>
            <p className={styles.license_text}>Я согласен с</p>
            <Link
              className={styles.license_link}
              href={routes.LICENSE}
              target="_blank"
              rel="noopener noreferrer"
            >
              Условиями соглашения
            </Link>
            <input onChange={onChangeCheckbox} className={styles.checkbox} type="checkbox"></input>
          </div>
          <button disabled={disabled} type="submit" className={styles.button_submit}>
            Зарегистрироваться
          </button>
        </form>
        <div className={styles.register_wrap}>
          <p className={styles.register_desc}>Уже есть аккаунт?</p>
          <a className={styles.register_button} href="/" onClick={onClickLogin}>
            Войти
          </a>
        </div>
      </div>
    </dialog>
  ) : (
    <></>
  );
}
