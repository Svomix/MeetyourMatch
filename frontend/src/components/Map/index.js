'use client';
import { useEffect, useRef } from 'react';
import Script from 'next/script';
import styles from './index.module.css';

export default function Map() {
  const mapElem = useRef();

  function onLoad() {
    var map;

    DG.then(function () {
      map = DG.map(mapElem.current, {
        center: [53.211863, 50.17799],
        zoom: 13
      });

      DG.marker([53.211863, 50.178]).addTo(map).bindPopup('Я тут!');

      DG.marker([53.201184, 50.108233]).addTo(map).bindPopup('А тут кто!');
    });
  }

  return (
    <>
      <Script src="https://maps.api.2gis.ru/2.0/loader.js?pkg=full&skin=Dark" onReady={onLoad} />
      <div ref={mapElem} className={styles.map}></div>
    </>
  );
}
