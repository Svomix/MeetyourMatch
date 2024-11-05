import { NextResponse } from 'next/server';
import { tokenType } from './services/authService';

export function middleware(request) {
  let cookie = request.cookies.get(tokenType.ACCESS_TOKEN);
  return cookie ? NextResponse.next() : NextResponse.rewrite(new URL('/not-found', request.url));
}

export const config = {
  matcher: '/profile/:path*'
};
