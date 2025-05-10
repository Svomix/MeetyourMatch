'use client';
import Map from '@components/Map';
import { fetchAllLocations } from '@store/locationStore';
import { ModalPage, setModal } from '@store/modalSlice/index';
import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import styles from './index.module.css';

export default function PlaceEditor() {
  const dialog = useRef();
  const dispatch = useDispatch();
  const locations = useSelector((state) => state.locationsInfo);

  useEffect(() => {
    dispatch(fetchAllLocations());
  }, []);

  const [selected, setSelected] = useState(null);

  useEffect(() => {
    dialog.current?.showModal();

    function clickEvent(e) {
      let clickInside = dialog.current?.contains(e.target) && e.target !== dialog.current;
      if (!clickInside) dispatch(setModal(ModalPage.None));
    }

    document.addEventListener('mousedown', clickEvent);
    return () => {
      document.removeEventListener('mousedown', clickEvent);
    };
  }, []);

  return (
    <dialog ref={dialog} className={styles.dialog}>
      <div className={styles.dialog_wrap}>
        <h1 className={styles.title}>Выбрать место</h1>
        <div className={styles.map_container}>
          <Map selected={selected} onSelect={setSelected} />
        </div>
        <p className={styles.register_desc}>{selected && selected.title}</p>
        <button className={styles.button_submit}>+ Создать место</button>
        <button className={styles.button_submit}>Выбрать</button>
      </div>
    </dialog>
  );
}
