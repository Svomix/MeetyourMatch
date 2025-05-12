'use client';
import mock_logo from '@public/user_logo.jpg';
import Image from 'next/image';
import styles from './index.module.css';

export default ({ person }) => {
  console.log(person);
  return (
    <div className={styles.wrapper}>
      <div className={styles.person}>
        <Image
          alt={'person img'}
          className={styles.img}
          src={person.avatarPath || mock_logo}
          width={250}
          height={250}
        />
        <h2 className={styles.name}>{person.username}</h2>
      </div>
    </div>
  );
};
