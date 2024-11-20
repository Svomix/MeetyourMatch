'use client';
import { unauth_fetch } from '@/utils/fetch';
import routes from '@routes';
import { ModalPage, setModal } from '@store/slices/modalSlice';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';
import InputField from '../InputField';
import styles from './index.module.css';

export default function Register() {
  const dialog = useRef();
  const router = useRouter();
  const dispatch = useDispatch();

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

  async function onClickRegister(e) {
    e.preventDefault();

    if (e.target[2].value != e.target[3].value) {
      alert('Пароли не совпадают');
      return;
    }

    await unauth_fetch('/api/register', 'post', new FormData(e.target));

    dispatch(setModal(ModalPage.None));
    router.refresh();
  }

  function onClickLogin(e) {
    e.preventDefault();
    dispatch(setModal(ModalPage.Login));
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
          <InputField placeholder="Имя" name="username" />
          <InputField type="email" placeholder="E-mail" name="email" />
          <InputField type="password" placeholder="Пароль" name="password" />
          <InputField type="password" placeholder="Повторите пароль" />
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
