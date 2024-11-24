'use client';
import { tokenType } from '@/services/authService';
import { authed, unauthed } from '@/services/axiosInstance';
import CardInfo from '@components/CardInfo';
import Cookies from 'js-cookie';
import { useEffect, useState } from 'react';

export default function EventsPage({ params }) {
  const slug = params.id;

  let [event, setEvent] = useState(undefined);

  useEffect(() => {
    const instance = Cookies.get(tokenType.ACCESS_TOKEN) ? authed : unauthed;
    instance.get(`/v1/events/${slug}`).then(response => setEvent(response.data))
  }, []);

  console.log(event);
  

  return event && <CardInfo path={slug} event={event} />;
}
