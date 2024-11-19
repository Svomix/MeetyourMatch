'use client';
import { auth_fetch } from '@/utils/fetch';
import routes from '@routes';
import { useRouter } from 'next/navigation';

export default () => {
  const router = useRouter();

  async function onClick(e) {
    e.preventDefault();
    await auth_fetch('/api/logout');
    //removeAccessToken();
    router.replace(routes.HOME);
  }

  return <button onClick={onClick}>Выйти</button>;
};
