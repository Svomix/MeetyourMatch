'use client';
import Card from '@components/Card';
import { useState } from 'react';
import styles from './index.module.css';

export default ({ setActive, onDragLeft, onDragRight, event, width = 350, height = 500 }) => {
  const [start, setStart] = useState({ x: 0, y: 0 });
  const [offset, setOffset] = useState({ x: 0, y: 0 });

  const handleMouseDown = (e) => {
    console.log('dowm');
    setStart({ x: e.clientX, y: e.clientY });
  };

  const handleMouseMove = (e) => {
    if (start.x == 0 && start.y == 0) return;

    const maxX = (window.innerWidth - width * 1.04) / 2;
    const maxY = (window.innerHeight - height * 1.04) / 2;

    setOffset({
      x: Math.min(Math.max(e.clientX - start.x, -maxX), maxX),
      y: Math.min(Math.max(e.clientY - start.y, -maxY - 65), maxY - 65)
    });

    if (offset.x < -10) setActive(1);
    else if (offset.x > 10) setActive(2);
    else setActive(0);
  };

  const handleMouseUp = (e) => {
    console.log('up');
    if (offset.x > 300) onDragRight();
    else if (offset.x < -300) onDragLeft();
  };

  const handleClick = (e) => {
    console.log('click');
    if (Math.abs(offset.x) > 10 || Math.abs(offset.y) > 10) e.preventDefault();
    setStart({ x: 0, y: 0 });
    setOffset({ x: 0, y: 0 });
    setActive(0);
  };

  return (
    <Card
      className={styles.card}
      event={event}
      width={width}
      height={height}
      onClick={handleClick}
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
      style={{
        top: `${offset.y}px`,
        left: `${offset.x}px`,
        transition: `${start.x == 0 && start.y == 0 && '200ms ease-in-out'}`
      }}
    />
  );
};
