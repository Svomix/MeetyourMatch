'use client';
import { ModalPage, setModal, setModalData } from '@store/modalSlice/index';
import { fetchAllLocations } from '@store/locationStore';
import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import styles from './index.module.css';
import Map from '@components/Map';
import classNames from '@/utils/classnames';

export default function PlaceEditor() {
  const dialog = useRef();
  const dispatch = useDispatch();
  const locations = useSelector((state) => state.locationsInfo);

  useEffect(() => {
    dispatch(fetchAllLocations());
  }, []);

  const [selected, setSelected] = useState(null);

  const onSubmit = (e) => {
    e.preventDefault()
    dispatch(setModalData({key: 'currentLocation', data: selected}))
    dispatch(setModal(ModalPage.None))
  }

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
      <div className={classNames(styles.dialog_wrap, styles.gap_8)}>
        <h1 className={styles.title}>Выбрать место</h1>
        <p className={styles.selected_place}>{selected && selected.title}</p>
        <div className={styles.map_container}>
          <Map editable data={locations} selected={selected} onSelect={setSelected}/>
        </div>
        <button onClick={onSubmit} className={styles.button_submit}>
          Выбрать
        </button>
      </div>
    </dialog>
  );
}
