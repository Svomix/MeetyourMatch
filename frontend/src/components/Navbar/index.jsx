import logo from '@public/Logo.svg';
import routes from '@routes';
import Image from 'next/image';
import Link from 'next/link';
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
          <Link className={styles.nav_link} href={routes.HOME}>
            Главная
          </Link>
        </li>
        <li>
          <Link className={styles.nav_link} href={routes.POSTER}>
            Афиша
          </Link>
        </li>
        <li>
          <Link className={styles.nav_link} href={routes.HOBBY}>
            Хобби
          </Link>
        </li>
        <li>
          <Link className={styles.nav_link} href={routes.MAP}>
            Карта
          </Link>
        </li>
      </ul>
    </nav>
  );
};
