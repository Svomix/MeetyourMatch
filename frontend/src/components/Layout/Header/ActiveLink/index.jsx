'use client';
import classNames from '@/utils/classnames';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import styles from './index.module.css';

export default ({ className, href, children, onClick }) => {
  const path = usePathname();
  return (
    <Link
      className={classNames(className, styles.link, path == href && styles.active_link)}
      onClick={onClick}
      href={href}
    >
      {children}
    </Link>
  );
};
