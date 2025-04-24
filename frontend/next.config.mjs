/** @type {import('next').NextConfig} */

const nextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: process.env.BACKEND_URL || 'http://localhost:8080/api/:path*'
      },
      {
        source: '/static/:path*',
        destination: process.env.S3_URL || 'http://localhost:9000/:path*'
      }
    ];
  },
  images: {
    localPatterns: [
      {
        pathname: '/static/**',
        search: '',
      },
    ],
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
  output: "standalone"
};

export default nextConfig;
