'use client';
import { authStates, setAuth } from '@store/authSlice';
import { ModalPage, setModal } from '@store/modalSlice/index';
import { useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';
import InputField from '../InputField';
import styles from './index.module.css';
import { unauthed } from '@/services/axiosInstance';

export default function Login() {
  const dialog = useRef();
  const dispatch = useDispatch();
  const router = useRouter();

  const [fetching, setFetching] = useState(false)
  const [error, setError] = useState("")

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

  function onClickRegister(e) {
    dispatch(setModal(ModalPage.Register));
    e.preventDefault();
  }

  async function onSubmit(e) {
    e.preventDefault();
    setFetching(true)
    try{
      await unauthed.post("/login", new FormData(e.target))
      setFetching(false)
      dispatch(setModal(ModalPage.None));
      dispatch(setAuth(authStates.auth));
      router.refresh();
    }catch(e){
      if(e.response.data.exception === "BadCredentialsException"){
        setError(e.response.data.error)
        await new Promise(r => setTimeout(r, 2000))
      }else{
        alert(e)
      }
      setFetching(false)
    }
  }

  function resetError(){
    if(!fetching && error){
      setError("")
    }
  }

  return (
    <dialog ref={dialog} className={styles.dialog}>
      <div className={styles.dialog_wrap}>
        <h1 className={styles.title}>Вход</h1>
        <form onSubmit={onSubmit} className={styles.form}>
          <InputField placeholder="Имя" name="username" onFocus={resetError} />
          <InputField type="password" placeholder="Пароль" name="password" onFocus={resetError}  />
          {error && <p className={styles.error}>{error}</p>}
          <button type="submit" disabled={fetching} className={styles.button_submit}>
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
