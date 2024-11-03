'use client'
import Image from 'next/image';
import styles from './index.module.css';
import { removeAccessToken } from '@/services/authService';
import { useRouter } from 'next/navigation';

export default () => {
  const router = useRouter();

  function onClick(){
    removeAccessToken()
    router.refresh()
  }

  return (<div className={styles.signed_container}>
    <button onClick={onClick} className={styles.notifIcon}></button>
    <button className={styles.calenderIcon}></button>
    <p className={styles.username}>Username</p>
    <Image className={styles.userLogo} src={"/user_logo.jpg"} width={48} height={48} alt='User logo'></Image>
  </div>);
};
