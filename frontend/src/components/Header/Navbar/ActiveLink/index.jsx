'use client';
import classNames from '@/utils/classnames';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import styles from './index.module.css';
import routes from '@routes';

export default ({ href, children }) => {
  const path = usePathname();
  return (
    <Link
      className={classNames(
        styles.link,
        (href === routes.HOME ? path == routes.HOME : path.startsWith(href)) && styles.active_link
      )}
      href={href}
    >
      {children}
    </Link>
  );
};
