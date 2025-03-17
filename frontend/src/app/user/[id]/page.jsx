'use client';
import { tokenType } from '@/services/authService';
import { authed, unauthed } from '@/services/axiosInstance';
import Cookies from 'js-cookie';
import { useEffect, useState } from 'react';
import PersonInfo from '@components/PersonInfo';

export default ({ params }) => {
  let [person, setPerson] = useState(undefined);
  const slug = params.id;

  useEffect(() => {
    const instance = Cookies.get(tokenType.ACCESS_TOKEN) ? authed : unauthed;
    instance.get(`/v1/users/${slug}`).then((response) => setPerson(response.data));
  }, []);

  return person && <PersonInfo person={person} />;
};
