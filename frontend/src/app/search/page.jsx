'use client';
import { getIsLoggedIn, tokenType } from '@/services/authService';
import { authed, unauthed } from '@/services/axiosInstance';
import Card from '@components/Card';
import CardNavigation from '@components/Pagination';
import SDropdown from '@components/SDropdown';
import Search from '@components/Search';
import routes from '@routes';
import { fetchAllTags } from '@store/tagStore';
import Cookies from 'js-cookie';
import Link from 'next/link';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { Suspense, useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import styles from './page.module.css';

const SearchSection = () => {
  let [events, setEvents] = useState([]);
  let [totalPages, setTotalPages] = useState(1);
  let [squery, setSQuery] = useState('');

  const pathname = usePathname();
  const searchParams = useSearchParams();
  const router = useRouter();

  const page = parseInt(searchParams.get('page')) || 1;
  const query = searchParams.get('q') || '';

  const setParam = (key, value) => {
    const current = new URLSearchParams(Array.from(searchParams.entries()));
    current.set(key, value);
    const search = current.toString();
    const query = search ? `?${search}` : '';
    router.push(`${pathname}${query}`);
  };

  const setQuery = (query) => setParam('q', query);
  const setPage = (page) => setParam('page', page);

  const dispatch = useDispatch();
  const tags = useSelector((state) => state.tagsInfo) || [];

  useEffect(() => {
    dispatch(fetchAllTags());
    setSQuery(query);
  }, []);

  const getSuggestions = (text) => {
    const split = text.split(/\s/);
    if (split.length == 0) return;
    const last = split[split.length - 1];
    const nolast = text.substr(0, text.length - last.length);
    if (last.startsWith('#')) {
      const taglist = tags.map((t) => ({
        name: '#' + t.name,
        query: '#' + t.name.toLowerCase(),
        rep: nolast + '#' + t.name
      }));
      return taglist.filter((t) => t.query.startsWith(last));
    }
  };

  const suggestions = getSuggestions(squery) || [];

  const onSearchChange = (e) => {
    e.preventDefault();
    setSQuery(e.target.value);
  };

  const onSearch = (e) => {
    e.preventDefault();
    setQuery(squery);
  };

  useEffect(() => {
    const instance = Cookies.get(tokenType.ACCESS_TOKEN) ? authed : unauthed;
    instance
      .get(`/v1/events?limit=16&page=${page}`, {
        params: {
          limit: 16,
          page: page,
          s: query
        }
      })
      .then((response) => {
        setEvents(response.data.content);
        setTotalPages(response.data.page.totalPages);
      });
  }, [page, query]);

  return (
    <>
      <section className={styles.controls}>
        <Search
          placeholder="Название,  описание,  #тег"
          value={squery}
          onChange={onSearchChange}
          onSearch={onSearch}
          suggestions={suggestions}
        />
        <SDropdown
          placeholder={'Платно?'}
          data={[
            { key: 'pay', text: 'Платно' },
            { key: 'free', text: 'Бесплатно' }
          ]}
          className={styles.dbar}
        />
        {getIsLoggedIn() && (
          <Link className={styles.create_event} href={routes.CREATE_EVENT}>
            Создать событие
          </Link>
        )}
      </section>
      <div className={styles.events}>
        {events?.map((el, index) => (
          <Card key={el.id} event={el} />
        ))}
      </div>
      <div className={styles.pages}>
        <CardNavigation current={page} setCurrent={setPage} total={totalPages} />
      </div>
    </>
  );
};

export default () => {
  return (
    <>
      <div className={styles.wrapper}>
        <Suspense>
          <SearchSection />
        </Suspense>
      </div>
    </>
  );
};
