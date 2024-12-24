'use client';
import svg2 from '@public/check-circle-svgrepo-com.svg';
import { ModalPage, setModal } from '@store/modalSlice/index';
import Image from 'next/image';
import { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';
import base_styles from '../index.module.css';
import styles from './verify.module.css';
import InputField from '@components/InputField';
import { unauthed } from '@/services/axiosInstance';
import { fetchProfileInfo } from '@store/profileSlice';
import { useRouter } from 'next/navigation';

export default function Verify() {
  const dialog = useRef();
  const dispatch = useDispatch();
  const router = useRouter();
  const [error, setError] = useState('');

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

  async function onSubmit(e){
    e.preventDefault()
    
    let form = new FormData(e.target);
    form.append("email", localStorage.getItem("temp_email"))
    try{
      await unauthed.post('/register/verify', form);

      localStorage.removeItem("temp_email")
      dispatch(fetchProfileInfo())
      dispatch(setModal(ModalPage.None))
      router.refresh();
    }catch(e){
      setError(e.response.data.error)
    }
  }

  function resetError() {
    if (error) {
      setError('');
    }
  }

  return open ? (
    <dialog ref={dialog} className={base_styles.dialog}>
      <form onSubmit={onSubmit} className={base_styles.dialog_wrap}>
        <h1 className={base_styles.title}>Потверждение почты</h1>
        <h2 className={styles.desc}>На вашу почту выслано письмо. Скопируйте код из письма в поле ниже.</h2>
        {/* <Image className={styles.image} alt="verify" src={svg2}></Image> */}
        <InputField onFocus={resetError} className={styles.code} placeholder="Код" name="token" />
        {error && <p className={base_styles.error}>{error}</p>}
        <button type="submit" className={styles.button_submit}>
          Подтвердить
        </button>
      </form>
    </dialog>
  ) : (
    <></>
  );
}
