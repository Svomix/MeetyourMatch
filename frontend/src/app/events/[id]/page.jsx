'use client';
import CardInfo from '@components/CardInfo';
import { fetchEventInfo } from '@store/eventStore';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';

export default function EventsPage({ params }) {
  const slug = params.id;
  const dispatch = useDispatch();

  const info = useSelector((state) => state.eventInfo);

  useEffect(() => {
    dispatch(fetchEventInfo(slug));
  }, []);

  return info && <CardInfo path={slug} event={info.data} />;
}
