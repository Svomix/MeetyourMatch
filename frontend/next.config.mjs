/** @type {import('next').NextConfig} */

const nextConfig = {
  async rewrites() {
    return [
      {
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
      }
    ]
  },

  skipTrailingSlashRedirect: true
};
export default nextConfig;
