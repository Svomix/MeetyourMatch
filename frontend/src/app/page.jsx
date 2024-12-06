import { tokenType } from '@/services/authService';
import SingedMain from '@components/MainPage/SingedMain';
import UnsignedMain from '@components/MainPage/UnsignedMain';
import mock_img from '@public/mock_img.jpg';
import { cookies } from 'next/headers';

export default async () => {
  const cookieStore = await cookies();
  const token = cookieStore.get(tokenType.ACCESS_TOKEN);

  return <>{token?.value ? <SingedMain events={events} /> : <UnsignedMain events={events} />}</>;
};

const events = [
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    coverImgUrl: mock_img,
    id: 1
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    coverImgUrl: mock_img,
    id: 2
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    coverImgUrl: mock_img,
    id: 3
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    coverImgUrl: mock_img,
    id: 4
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    coverImgUrl: mock_img,
    id: 5
  },
  {
    title: 'Lorem, ipsum dolor.',
    date: '17:00 01.01.2024',
    tags: '#Lorem #ipsum #dolor',
    coverImgUrl: mock_img,
    id: 6
  }
];
