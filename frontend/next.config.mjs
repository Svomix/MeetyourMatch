/** @type {import('next').NextConfig} */

const nextConfig = {
  async rewrites() {
    return [
      (!process.env.BACKEND_URL) && {
        source: '/api/:path*',
        destination: 'http://localhost:8080/api/:path*'
      }
    ];
  },
  images: {
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
