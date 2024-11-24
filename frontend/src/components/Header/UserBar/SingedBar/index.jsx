'use client';
import { auth_fetch } from '@/utils/fetch';
import bell_svg from '@public/Bell.jsx';
import active_bell_svg from '@public/BellActive.jsx';
import calendar_svg from '@public/Calendar';
import active_calendar_svg from '@public/CalendarActive';
import { default as Routes, default as routes } from '@routes';
import Image from 'next/image';
import { usePathname } from 'next/navigation';
import { useEffect, useState } from 'react';
import ActiveLink from '../../ActiveLink';
import styles from './index.module.css';

export default () => {
  const path = usePathname();
  let [name, setName] = useState('username');

  useEffect(() => {
    auth_fetch('/api/account/getInfo')
      .then((el) => el.json())
      .then((el) => setName(el.username));
  });

  return (
    <div className={styles.signed_container}>
      <ActiveLink href={Routes.NOTIFY}>
        {path == routes.BELL ? active_bell_svg : bell_svg}
      </ActiveLink>
      <ActiveLink href={Routes.CALENDAR}>
        {path == routes.CALENDAR ? active_calendar_svg : calendar_svg}
      </ActiveLink>
      <ActiveLink className={styles.profile_link} href={Routes.PROFILE}>
        <p className={styles.username}>{name}</p>
        <Image
          className={styles.userLogo}
          src={'/user_logo.jpg'}
          width={48}
          height={48}
          alt="User logo"
        ></Image>
      </ActiveLink>
    </div>
  );
};
