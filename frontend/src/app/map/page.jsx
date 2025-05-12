'use client'
import Map from '@components/Map';
import SDropdown from '@components/SDropdown';
import Search from '@components/Search';
import styles from './page.module.css';
import { useEffect, useState } from 'react';
import { authed, unauthed } from '@/services/axiosInstance';
import MapEventCard from '@components/Map/MapEventCard';
import { useDispatch, useSelector } from 'react-redux';
import { fetchAllLocations } from '@store/locationStore';

export default function MapPage() {
  const dispatch = useDispatch()
  const data = useSelector((state) => state.locationsInfo)
  //const [data, setData] = useState([])

  const [selected, setSelected] = useState(null)

  useEffect(() => {
    dispatch(fetchAllLocations())
  }, [])

  const onRemovePoint = async (e) => {
    e.preventDefault()
    await authed.delete(`maps/locations/${selected.id}`)
    dispatch(fetchAllLocations())
    setSelected(null)
  }

  return (
    <>
      <div className={styles.wrap}>
        <section className={styles.controls}>
          <Search placeholder="Поиск" />
          <SDropdown
            placeholder={'Платно?'}
            data={[
              { key: 'pay', text: 'Платно' },
              { key: 'free', text: 'Бесплатно' }
            ]}
            className={styles.dbar}
          />
        </section>
        <section className={styles.map_container}>
          <div className={styles.map_cards_menu}>
          <div className={styles.map_cards_container}>
            <div className={styles.map_cards}>
                  {selected && <div className={styles.place_container}>{selected.title}</div>}
                  
                  {selected && selected.events.map((el) => (
                    <MapEventCard key={el.id} data={el}/>
                  ))}
            </div>
          </div>
          {selected?.events.length === 0 && <button onClick={onRemovePoint} className={styles.map_del_button}>Удалить точку</button>}
          </div>
          <Map data={data} selected={selected} onSelect={setSelected}/>
        </section>
      </div>
    </>
  );
}