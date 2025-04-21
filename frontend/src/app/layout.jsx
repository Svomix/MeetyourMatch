import Providers from '@/store/provider';
import Layout from '@components/Layout';
import localFont from 'next/font/local';
import './globals.css';

export default function RootLayout({ children }) {
  return (
    <html lang="ru">
      <body className={sansation.className}>
        <Providers>
          <Layout>{children}</Layout>
        </Providers>
      </body>
    </html>
  );
}

const sansation = localFont({
  src: [
    {
      path: '../../public/Fonts/Sansation-Light.ttf',
      weight: '100 300',
      style: 'light'
    },
    {
      path: '../../public/Fonts/Sansation-Regular.ttf',
      weight: '400 600',
      style: 'regular'
    },
    {
      path: '../../public/Fonts/Sansation-Bold.ttf',
      weight: '700 900',
      style: 'bold'
    },
    {
      path: '../../public/Fonts/Sansation-Italic.ttf',
      weight: '400 600',
      style: 'italic'
    }
  ],
  variable: '--sansation'
});

export const metadata = {
  title: 'Meet your match',
  description: 'We will help you to find your interests'
};
