'use client';
import { unauthed } from '@/services/axiosInstance';
import routes from '@routes';
import { setAuth } from '@store/authSlice';
import { ModalPage, setModal } from '@store/modalSlice/index';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';
import InputField from '../../InputField';
import styles from './index.module.css';

export default function Register() {
  const dialog = useRef();
  const router = useRouter();
  const dispatch = useDispatch();

  const [fetching, setFetching] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    dialog.current?.showModal();

    function clickEvent(e) {
      let clickInside = dialog.current?.contains(e.target) && e.target !== dialog.current;
      if (!clickInside) dispatch(setModal(ModalPage.None));
    }

    document.addEventListener('mousedown', clickEvent);

    return () => {
      document.removeEventListener('mousedown', clickEvent);
    };
  }, []);

  async function onClickRegister(e) {
    e.preventDefault();

    if (e.target[2].value != e.target[3].value) {
      setError('Пароли не совпадают');
      return;
    }

    setFetching(true);
    try {
      await unauthed.post('/register', new FormData(e.target));
      localStorage.setItem("temp_email", e.target[1].value)
      dispatch(setModal(ModalPage.Verify));
      dispatch(setAuth(true));
      router.refresh();
    } catch (e) {
      if (e.response.data.exception === 'UserAlreadyExistsException') {
        setError(e.response.data.error);
      } else if (e.response.status == 400) {
        setError('Заполните все поля');
      } else {
        alert(e);
      }
      await new Promise((r) => setTimeout(r, 2000));
      setFetching(false);
    }
  }

  function onClickLogin(e) {
    e.preventDefault();
    dispatch(setModal(ModalPage.Login));
  }

  const [disabled, setDisabled] = useState(true);

  function onChangeCheckbox(e) {
    setDisabled(!e.target.checked);
  }

  function resetError() {
    if (!fetching && error) {
      setError('');
    }
  }

  return open ? (
    <dialog ref={dialog} className={styles.dialog}>
      <div className={styles.dialog_wrap}>
        <h1 className={styles.title}>Регистрация</h1>
        <form onSubmit={onClickRegister} className={styles.form}>
          <InputField placeholder="Имя" name="username" onFocus={resetError} />
          <InputField type="email" placeholder="E-mail" name="email" onFocus={resetError} />
          <InputField type="password" placeholder="Пароль" name="password" onFocus={resetError} />
          <InputField type="password" placeholder="Повторите пароль" onFocus={resetError} />
          {error && <p className={styles.error}>{error}</p>}
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
          <button disabled={disabled || fetching} type="submit" className={styles.button_submit}>
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
