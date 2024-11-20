'use client';
import { tokenType } from '@/services/authService';
import { auth_fetch, unauth_fetch } from '@/utils/fetch';
import CardInfo from '@components/CardInfo';
import Cookies from 'js-cookie';
import { useEffect, useState } from 'react';

export default function EventsPage({ params }) {
  const slug = params.id;

  let [event, setEvent] = useState(undefined);
  useEffect(() => {
    if (Cookies.get(tokenType.ACCESS_TOKEN))
      event = auth_fetch(`/api/v1/events/${slug}`)
        .then((resp) => resp.json())
        .then((data) => setEvent(data));
    else
      event = unauth_fetch(`/api/v1/events/${slug}`)
        .then((resp) => resp.json())
        .then((data) => setEvent(data));
  }, []);

  return <CardInfo path={slug} event={event} />;
}
