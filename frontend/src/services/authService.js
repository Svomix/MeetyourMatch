import Cookies from 'js-cookie';

export const tokenType = {
  ACCESS_TOKEN: 'accessToken',
  REFRESH_TOKEN: 'refreshToken'
};

export const getIsLoggedIn = () => {
  return Boolean(Cookies.get(tokenType.ACCESS_TOKEN));
};

export const getAccessToken = () => {
  return Cookies.get(tokenType.ACCESS_TOKEN) || null;
};

export const saveAccessToken = (accessToken) => {
  Cookies.set(tokenType.ACCESS_TOKEN, accessToken, {
    sameSite: 'strict',
    expires: 1
  });
};

export const removeAccessToken = () => {
  Cookies.remove(tokenType.ACCESS_TOKEN);
};
