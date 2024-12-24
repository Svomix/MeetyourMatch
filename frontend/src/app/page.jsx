import { tokenType } from '@/services/authService';
import SingedMain from '@components/MainPage/SingedMain';
import UnsignedMain from '@components/MainPage/UnsignedMain';
import { cookies } from 'next/headers';

export default async () => {
  const cookieStore = await cookies();
  const token = cookieStore.get(tokenType.ACCESS_TOKEN);

  return <>{token?.value ? <SingedMain /> : <UnsignedMain />}</>;
};
