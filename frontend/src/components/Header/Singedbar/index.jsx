'use client';
import { removeAccessToken } from '@/services/authService';
import Routes from '@routes';
import Image from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import styles from './index.module.css';

export default () => {
  const router = useRouter();

  function onClick() {
    removeAccessToken();
    router.refresh();
  }

  return (
    <div className={styles.signed_container}>
      <button onClick={onClick} className={styles.notifIcon}></button>
      <Link className={styles.calenderIcon} href={Routes.CALENDAR} />
      <Link className={styles.profile_link} href={Routes.PROFILE}>
        <p className={styles.username}>Username</p>
        <Image
          className={styles.userLogo}
          src={'/user_logo.jpg'}
          width={48}
          height={48}
          alt="User logo"
        ></Image>
      </Link>
    </div>
  );
};
