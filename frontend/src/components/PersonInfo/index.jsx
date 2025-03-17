'use client';
import styles from './index.module.css';
import mock_logo from '../../../public/user_logo.jpg';
import Image from 'next/image';
import Link from 'next/link';
import { ModalPage, setModal } from '@store/modalSlice/index';
import { useDispatch } from 'react-redux';
import { getIsLoggedIn } from '@/services/authService';

export default ({ person }) => {
  console.log(person);
  const dispatch = useDispatch();

  const onClick = (e) => {
    if (!getIsLoggedIn()) {
      e.preventDefault();
      dispatch(setModal(ModalPage.Login));
    }
  };

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
      <div className={styles.desc_wrapper}>
        <p className={styles.about}>
          <span className={styles.accent}>Про&nbsp;себя:&nbsp;</span>
          {person.description}
        </p>
        <span className={styles.gender}>
          <span className={styles.accent}>Пол:&nbsp;</span>
          {person.gender}
        </span>
        <span className={styles.city}>
          <span className={styles.accent}>Город:&nbsp;</span>
          {person?.city.name}
        </span>
      </div>
      <Link href={`/`} onClick={onClick} className={styles.write}>
        Написать
      </Link>
    </div>
  );
};
