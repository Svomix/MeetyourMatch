import './globals.css';

export const metadata = {
  title: 'Meet your match',
  description: 'We will help you to find your interests'
};

export default function RootLayout({ children }) {
  return (
    <html lang="ru">
      <body>{children}</body>
    </html>
  );
}
