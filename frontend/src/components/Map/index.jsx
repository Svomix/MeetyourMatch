'use client';
import { useEffect, useRef, useState } from 'react';
import Script from 'next/script';
import styles from './index.module.css';
import classNames from '@/utils/classnames';

export default function Map({data, selected, onSelect, editable}) {
  const mapElem = useRef();

  const [map, setMap] = useState(null)
  const [markers , setMarkers] = useState(null)
  const [newMarker , setNewMarker] = useState(null)
  const [newMarkerName, setNewMarkerName] = useState("")

  const fakeMarker = (marker, name) => {
    onSelect({
        "id": "?",
        "title": name,
        "address": "",
        "latitude": marker._latlng.lat,
        "longitude": marker._latlng.lng,
        "events": []
      })
  }

  const onChangeNewMarkerName = (e) => {
    setNewMarkerName(e.target.value);
    if(newMarker){
      fakeMarker(newMarker, e.target.value)
    }
  };

  useEffect(() => {
    if(!map) return

    markers.removeFrom(map);
    const new_markers = DG.featureGroup()
    
    data && data.forEach(element => {
      if(!element || element.latitude === undefined || element.longitude === undefined) return
      let myDivIcon = DG.divIcon({
        iconSize: [30, 30],
        html: element.events?.length?.toString(),
        className: classNames(styles.icon, selected && selected.id === element.id && styles.icon_active)
      });

      DG.marker([element.latitude, element.longitude], {icon: myDivIcon, title: element.title}).addTo(new_markers).on("click", function() {
        newMarker && newMarker.removeFrom(map);
        setNewMarker(null)
        onSelect && onSelect(element)
      });
    });

    new_markers.addTo(map);
    setMarkers(new_markers)
    // map.fitBounds(markers.getBounds());
  }, [data, map, selected, newMarker])

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

  if(map){
    map.off("contextmenu")

    map.on("contextmenu", (e) => {
      if(editable){
        newMarker && newMarker.removeFrom(map);

        let myDivIcon = DG.divIcon({
          iconSize: [30, 30],
          html: '+',
          className: classNames(styles.icon, styles.icon_active)
        });

        const m = DG.marker(e.latlng, {icon: myDivIcon, title: 'Новое место'})
        
        m.addTo(map)
        setNewMarker(m)
        fakeMarker(m, newMarkerName)
      }
    })
  }

  return (
    <>
      <Script src="https://maps.api.2gis.ru/2.0/loader.js?pkg=full&skin=dark" onReady={onLoad} />
      <div ref={mapElem} className={styles.map}>
        {newMarker && <div className={styles.new_marker_container}>
          <div className={styles.new_marker_div}>
            <p className={styles.new_marker_text}>Новая точка</p>
            <input value={newMarkerName} onChange={onChangeNewMarkerName} placeholder='Название' className={styles.new_marker_input} type='text'></input>
          </div>
        </div>}
      </div>
      
    </>
  );

  
}
