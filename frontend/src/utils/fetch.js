import { tokenType } from '@/services/authService';
import Cookies from 'js-cookie';

export const auth_fetch = (url, method, body) =>
  fetch(url, {
    method: method,
    headers: { Authorization: 'Bearer ' + Cookies.get(tokenType.ACCESS_TOKEN) },
    body: body
  });

export const unauth_fetch = (url, method, body) =>
  fetch(url, {
    method: method,
    body: body
  });
