'use client';
import { useEffect, useRef, useState } from 'react';
import classNames from '@/utils/classnames';
import styles from './index.module.css';
import 'leaflet/dist/leaflet.css';

let L;

export default function Map({ data, selected, onSelect, editable }) {
  const mapElem = useRef(null);
  const [map, setMap] = useState(null);
  const [markers, setMarkers] = useState(null);
  const [newMarker, setNewMarker] = useState(null);
  const [newMarkerName, setNewMarkerName] = useState('');

  const fakeMarker = (marker, name) => {
    onSelect?.({
      id: '?',
      title: name,
      address: '',
      latitude: marker.getLatLng().lat,
      longitude: marker.getLatLng().lng,
      events: []
    });
  };

  const onChangeNewMarkerName = (e) => {
    setNewMarkerName(e.target.value);
    if (newMarker) fakeMarker(newMarker, e.target.value);
  };

  useEffect(() => {
    let isMounted = true;
    let instance;

    import('leaflet').then((module) => {
      if (!isMounted || !mapElem.current) return;
      L = module.default || module;

      instance = L.map(mapElem.current, {
        center: [53.211863, 50.17799],
        zoom: 13
      });

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap',
        maxZoom: 19
      }).addTo(instance);

      setMap(instance);
      setMarkers(L.featureGroup().addTo(instance));
      setTimeout(() => instance.invalidateSize(), 200);
    });

    return () => {
      isMounted = false;
      instance?.remove();
    };
  }, []);

  useEffect(() => {
    if (!map || !markers || !L) return;

    markers.clearLayers();

    data?.forEach((el) => {
      if (!el || el.latitude === undefined || el.longitude === undefined) return;

      const icon = L.divIcon({
        iconSize: [30, 30],
        html: (el.events?.length ?? 0).toString(),
        className: classNames(styles.icon, selected?.id === el.id && styles.icon_active)
      });

      L.marker([el.latitude, el.longitude], { icon, title: el.title })
        .addTo(markers)
        .on('click', () => {
          if (newMarker) {
            newMarker.remove();
            setNewMarker(null);
          }
          onSelect?.(el);
        });
    });
  }, [data, map, markers, selected, newMarker, onSelect]);

  useEffect(() => {
    if (!map || !L) return;

    map.off('contextmenu');

    if (editable) {
      map.on('contextmenu', (e) => {
        if (newMarker) newMarker.remove();

        const icon = L.divIcon({
          iconSize: [30, 30],
          html: '+',
          className: classNames(styles.icon, styles.icon_active)
        });

        const m = L.marker(e.latlng, { icon, title: 'Новое место' }).addTo(map);
        setNewMarker(m);
        fakeMarker(m, newMarkerName);
      });
    }
  }, [map, editable, newMarker, newMarkerName]);

  return (
    <div ref={mapElem} className={styles.map}>
      {newMarker && (
        <div className={styles.new_marker_container}>
          <div className={styles.new_marker_div}>
            <p className={styles.new_marker_text}>Новая точка</p>
            <input
              value={newMarkerName}
              onChange={onChangeNewMarkerName}
              placeholder="Название"
              className={styles.new_marker_input}
              type="text"
            />
          </div>
        </div>
      )}
    </div>
  );
}
