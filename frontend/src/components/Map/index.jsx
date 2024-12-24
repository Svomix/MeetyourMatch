'use client';
import { useEffect, useRef, useState } from 'react';
import Script from 'next/script';
import styles from './index.module.css';
import classNames from '@/utils/classnames';

export default function Map({data, selected, onSelect}) {
  const mapElem = useRef();

  const [map, setMap] = useState(null)
  const [markers , setMarkers] = useState(null)

  useEffect(() => {
    if(!map) return

    markers.removeFrom(map);
    const new_markers = DG.featureGroup()

    data.forEach(element => {
      let myDivIcon = DG.divIcon({
        iconSize: [30, 30],
        html: element.events.length.toString(),
        className: classNames(styles.icon, selected && selected.id === element.id && styles.icon_active)
      });

      DG.marker([element.latitude, element.longitude], {icon: myDivIcon, title: element.title}).addTo(new_markers).on("click", function() {
        onSelect && onSelect(element)
      });
    });

    new_markers.addTo(map);
    setMarkers(new_markers)
    // map.fitBounds(markers.getBounds());
  }, [data, map, selected])

  function onLoad() {
    var map;

    DG.then(function () {
      map = DG.map(mapElem.current, {
        center: [53.211863, 50.17799],
        zoom: 13
      });

      setMap(map)
      setMarkers(DG.featureGroup())
    })
  }

  return (
    <>
      <Script src="https://maps.api.2gis.ru/2.0/loader.js?pkg=full&skin=dark" onReady={onLoad} />
      <div ref={mapElem} className={styles.map}></div>
    </>
  );

  
}
