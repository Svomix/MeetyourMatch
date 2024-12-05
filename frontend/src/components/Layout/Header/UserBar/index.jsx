import { tokenType } from '@/services/authService';
import { cookies } from 'next/headers';
import SingedBar from './SingedBar';
import UnsignedBar from './UnsignedBar';

export default async () => {
  const cookieStore = await cookies();
  const token = cookieStore.get(tokenType.ACCESS_TOKEN);

  return <>{token?.value ? <SingedBar /> : <UnsignedBar />}</>;
};
