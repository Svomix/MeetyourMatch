'use client';
import bell_svg from '@public/Bell.jsx';
import active_bell_svg from '@public/BellActive.jsx';
import calendar_svg from '@public/Calendar';
import active_calendar_svg from '@public/CalendarActive';
import { default as Routes, default as routes } from '@routes';
import { ModalPage, setModal } from '@store/modalSlice';
import { fetchProfileInfo } from '@store/profileSlice';
import Image from 'next/image';
import { usePathname } from 'next/navigation';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import ActiveLink from '../../ActiveLink';
import styles from './index.module.css';

export default () => {
  const path = usePathname();
  const dispatch = useDispatch();

  const info = useSelector((state) => state.profileInfo);

  if (info && info.isEnabled === false) {
    dispatch(setModal(ModalPage.Verify));
  }

  useEffect(() => {
    dispatch(fetchProfileInfo());
  }, []);

  return (
    <div className={styles.signed_container}>
      <ActiveLink href={Routes.NOTIFY}>
        {path == routes.BELL ? active_bell_svg : bell_svg}
      </ActiveLink>
      <ActiveLink href={Routes.CALENDAR}>
        {path == routes.CALENDAR ? active_calendar_svg : calendar_svg}
      </ActiveLink>
      <ActiveLink className={styles.profile_link} href={Routes.PROFILE}>
        <p className={styles.username}>{info?.username}</p>
        <Image
          className={styles.userLogo}
          src={info?.avatarPath || '/user_logo.jpg'}
          width={48}
          height={48}
        />
      </ActiveLink>
    </div>
  );
};
