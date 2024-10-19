import logo from '@public/Logo.svg';
import routes from '@routes';
import Image from 'next/image';
import ActiveLink from './ActiveLink';
import styles from './index.module.css';

export default () => {
  return (
    <nav className={styles.nav}>
      <div className={styles.logo_wrapper}>
        <Image className={styles.logo} src={logo} alt="logo" />
        <span className={styles.title}>MeetYourMatch</span>
      </div>
      <ul className={styles.nav_list}>
        <li>
          <ActiveLink href={routes.HOME}>Главная</ActiveLink>
        </li>
        <li>
          <ActiveLink href={routes.POSTER}>Афиша</ActiveLink>
        </li>
        <li>
          <ActiveLink href={routes.HOBBY}>Хобби</ActiveLink>
        </li>
        <li>
          <ActiveLink href={routes.MAP}>Карта</ActiveLink>
        </li>
      </ul>
    </nav>
  );
};
