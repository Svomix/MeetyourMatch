/** @type {import('next').NextConfig} */

const nextConfig = {
  async rewrites() { //удалить/заменить это в продакшене (переменные среды тут не работают)
    return [
      {
        source: '/api/:path*',
        destination: 'http://localhost:8080/api/:path*'
      },
      {
        source: '/static/:path*',
        destination: 'http://localhost:9000/:path*'
      }
    ];
  },
  images: {
    unoptimized: true,
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'image.shutterstock.com',
        port: '',
        pathname: '**'
      },
      {
        protocol: 'https',
        hostname: 'avatars.mds.yandex.net',
        port: '',
        pathname: '**'
      }
    ]
  },
  skipTrailingSlashRedirect: true,
  output: 'standalone'
};

export default nextConfig;
