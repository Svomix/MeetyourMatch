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
  const [error, setError] = useState(null);
  const router = useRouter();

  const info = useSelector((state) => state.profileInfo);

  if (username === null && info) setUsername(info.username);

  const username_changed = username !== null && username !== info?.username;
  const can_save = username_changed;

  async function onSave(e) {
    e.preventDefault();
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
    dispatch(fetchProfileInfo());
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
          <p className={styles.param_name}>Имя пользователя</p>
          <InputField
            className={styles.input}
            value={username || ''}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Имя"
            name="name"
            onFocus={resetError}
          />

          <p className={styles.param_name}>Пароль</p>
          <InputField
            className={styles.input}
            defaultValue=""
            placeholder="Новый пароль"
            name="password"
            type="password"
            onFocus={resetError}
          />
          <InputField
            className={styles.input}
            defaultValue=""
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
