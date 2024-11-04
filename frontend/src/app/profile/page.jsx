import Layout from '@components/Layout';
import styles from './page.module.css';
import InputField from '@components/InputField';

export default function ProfilePage() {
  return (
    <Layout>
      <div className={styles.container}>
        <h1 className={styles.header}>Профиль</h1>
        <form className={styles.settings_container}>
          <div>
            <p className={styles.param_name}>Имя пользователя</p>
            <InputField
              className={styles.input}
              defaultValue="Username"
              placeholder="Имя"
              name="name"
            />
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
          <div></div>
        </form>
      </div>
    </Layout>
  );
}
