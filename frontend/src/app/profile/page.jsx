import LogoutButton from '@components/Buttons/LogoutButton';
import InputField from '@components/InputField';
import InterestsContainer from '@components/InterestsContainer';
import Image from 'next/image';
import styles from './page.module.css';

export default function ProfilePage() {
  return (
    <>
      <div className={styles.container}>
        <form className={styles.settings_container}>
          <div>
            <h1 className={styles.header}>Мой профиль</h1>
            <p className={styles.param_name}>Имя пользователя</p>
            <InputField
              className={styles.input}
              defaultValue="Username"
              placeholder="Имя"
              name="name"
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
              ></Image>
            </div>
            <p className={styles.param_name}>Пароль</p>
            <InputField
              className={styles.input}
              defaultValue=""
              placeholder="Новый пароль"
              name="password"
              type="password"
            />
            <InputField
              className={styles.input}
              defaultValue=""
              placeholder="Повторите пароль"
              name="password2"
              type="password"
            />
          </div>

          <div>
            <h1 className={styles.header}>Интересы</h1>
            <InterestsContainer />
          </div>
        </form>
        <LogoutButton />
      </div>
    </>
  );
}
