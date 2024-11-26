'use client';
import { authed } from '@/services/axiosInstance';
import LogoutButton from '@components/Buttons/LogoutButton';
import InputField from '@components/InputField';
import InterestsContainer from '@components/InterestsContainer';
import { fetchProfileInfo } from '@store/profileSlice';

import Image from 'next/image';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import styles from './page.module.css';

export default function ProfilePage() {
  const dispatch = useDispatch();
  const [username, setUsername] = useState(null);
  const [password, setPassword] = useState(null);
  const [password2, setPassword2] = useState(null);

  const [error, setError] = useState(null);

  const router = useRouter();

  const info = useSelector((state) => state.profileInfo);

  const username_changed = username !== null && username !== info.username;
  const password_changed = password && password2;

  const can_save = username_changed || password_changed;

  async function onSave(e) {
    e.preventDefault();
    if (password_changed) {
      if (password !== password2) {
        setError('Пароли не совпадают');
        setPassword2('');
        return;
      }
      try {
        await authed.post('/account/setPassword', null, {
          params: {
            password: password
          }
        });
      } catch (e) {
        alert(e);
      } finally {
        setPassword('');
        setPassword2('');
      }
    }

    if (username_changed) {
      try {
        await authed.post('/account/setName', null, {
          params: { username: username }
        });
      } catch (e) {
        if (e.response.data.exception === 'UserAlreadyExistsException')
          setError(e.response.data.error);
        else alert(e);
      }
    }

    await dispatch(fetchProfileInfo()).unwrap();

    router.refresh();
  }

  function resetError() {
    setError('');
  }

  return (
    <div className={styles.container}>
      <div className={styles.settings_container}>
        <div>
          <h1 className={styles.header}>Мой профиль</h1>
          <p className={styles.param_name}>Имя пользователя</p>
          <InputField
            className={styles.input}
            value={username || ''}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Имя"
            name="name"
            onFocus={resetError}
          />
          <p className={styles.param_name}>Аватар</p>
          <div className={styles.avatar_container}>
            <div className={styles.avatar_hover}>Изменить аватар</div>
            <Image
              src="/user_logo.jpg"
              alt="User avatar"
              width={128}
              height={128}
              className={styles.user_avatar}
            />
          </div>

          <p className={styles.param_name}>Пароль</p>
          <InputField
            className={styles.input}
            value={password || ''}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Новый пароль"
            name="password"
            type="password"
            onFocus={resetError}
          />

          <InputField
            className={styles.input}
            value={password2 || ''}
            onChange={(e) => setPassword2(e.target.value)}
            placeholder="Повторите пароль"
            name="password2"
            type="password"
            onFocus={resetError}
          />
          {error && <p className={styles.error}>{error}</p>}

          <button disabled={!can_save} onClick={onSave} className={styles.save_button}>
            Сохранить
          </button>
        </div>

        <div>
          <h1 className={styles.header}>Интересы</h1>
          <InterestsContainer />
        </div>
      </div>
      <LogoutButton />
    </div>
  );
}
