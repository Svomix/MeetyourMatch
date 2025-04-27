'use client';
import classNames from '@/utils/classnames';
import { LeftArrow, RightArrow } from '@components/Buttons/ArrowButtons';
import Card from '@components/Card';
import { useLayoutEffect, useRef, useState } from 'react';
import styles from './index.module.css';

const params = {
  gap: 0,
  step: 0,
  slide: 0,
  initialOffset: 0,
  initTouch: 0,
  lastTouch: 0,
  threshold: 0
};

export default ({ events, cardWidth, cardHeight }) => {
  // Стейт и функция для ручного рендера
  const [, setState] = useState();
  const reRender = () => setState((s) => !s);

  // Ссылки для присвоения стилей и снятия ширины
  const sliderRef = useRef(null);
  const wrapperRef = useRef(null);
  const cardRef = useRef(null);

  // Функции переключения между слайдами
  const toPrevSlide = () => {
    if (params.slide > 0) {
      params.slide--;
      moveSlide();
      reRender();
    }
  };

  const toNextSlide = () => {
    if (params.slide < events.length - 3) {
      params.slide++;
      moveSlide();
      reRender();
    }
  };

  const selectSlide = (slide) => {
    params.slide = slide;
    moveSlide();
    reRender();
  };

  // Функции свайпов пальцами
  function touchStart(e) {
    if (wrapperRef.current) wrapperRef.current.classList.add(styles.grab);
    params.initTouch = e.touches[0].clientX;
    params.lastTouch = e.touches[0].clientX;
  }

  function touchMove(e) {
    params.lastTouch = e.touches[0].clientX;
    moveSlide(params.lastTouch - params.initTouch);
  }

  function touchEnd() {
    if (wrapperRef.current) wrapperRef.current.classList.remove(styles.grab);
    params.lastTouch - params.initTouch > params.threshold ? toPrevSlide() : moveSlide();
    params.lastTouch - params.initTouch < -params.threshold ? toNextSlide() : moveSlide();
    params.initTouch = params.lastTouch = 0;
  }

  // Функции свайпов мышью
  function mouseDown(e) {
    if (wrapperRef.current) wrapperRef.current.classList.add(styles.grab);
    params.initTouch = e.clientX;
    params.lastTouch = e.clientX;
  }

  function mouseMove(e) {
    if (e.buttons === 1) {
      params.lastTouch = e.clientX;
      moveSlide(params.lastTouch - params.initTouch);
    }
  }

  function mouseUp() {
    if (wrapperRef.current) wrapperRef.current.classList.remove(styles.grab);
    params.lastTouch - params.initTouch > params.threshold ? toPrevSlide() : moveSlide();
    params.lastTouch - params.initTouch < -params.threshold ? toNextSlide() : moveSlide();
    params.initTouch = params.lastTouch = 0;
  }

  function keyUp(e) {
    if (e.key === 'ArrowLeft') {
      toPrevSlide();
      console.log('ArrowLeft');
    }
    if (e.key === 'ArrowRight') toNextSlide();
  }

  // Функция перерасчета параметров
  const resize = () => {
    if (wrapperRef.current && cardRef.current) {
      params.gap = (wrapperRef.current.offsetWidth - 3 * cardRef.current.offsetWidth) / 2 - 10;
      params.step = cardRef.current.offsetWidth + params.gap;
      params.threshold = cardRef.current.offsetWidth / 3;
      params.initialOffset = 0;
      moveSlide();
      reRender();
    }
  };

  // Функция сдвига слайдера
  const moveSlide = (move = 0) => {
    if (sliderRef.current)
      sliderRef.current.style.transform = `translate3d(${params.initialOffset - params.slide * params.step + move}px, 0, 0)`;
  };

  useLayoutEffect(() => {
    if (wrapperRef.current && cardRef.current) {
      // Получаем изначальные измерения
      resize();

      const slider = wrapperRef.current;
      // Слушатели нажатий
      slider.addEventListener('touchstart', touchStart);
      slider.addEventListener('touchmove', touchMove);
      slider.addEventListener('touchend', touchEnd);

      // Слушатели кликов
      slider.addEventListener('mousedown', mouseDown);
      slider.addEventListener('mousemove', mouseMove);
      window.addEventListener('mouseup', mouseUp);

      // Навигация по клавишам
      document.addEventListener('keyup', keyUp);

      // Слушатель изменений окна
      window.addEventListener('resize', resize);

      return () => {
        slider.removeEventListener('touchstart', touchStart);
        slider.removeEventListener('touchmove', touchMove);
        slider.removeEventListener('touchend', touchEnd);
        slider.removeEventListener('mousedown', mouseDown);
        slider.removeEventListener('mousemove', mouseMove);
        window.removeEventListener('mouseup', mouseUp);
        window.removeEventListener('resize', resize);
        document.removeEventListener('keyup', keyUp);
      };
    }
  }, [events]);

  return (
    <>
      {events && (
        <section className={styles.wrapper}>
          <LeftArrow width={52} height={52} onClick={toPrevSlide} disabled={params.slide == 0} />

          <div className={styles.slider} ref={wrapperRef}>
            <div className={styles.slider_line} style={{ gap: `${params.gap}px` }} ref={sliderRef}>
              {events.map((event) => (
                <Card
                  key={event.id}
                  event={event}
                  height={cardHeight}
                  width={cardWidth}
                  refLink={cardRef}
                  className={params.slide === 0 && styles.btn_dis}
                />
              ))}
            </div>
          </div>

          <RightArrow
            width={52}
            height={52}
            onClick={toNextSlide}
            disabled={params.slide == events.length - 3}
          />

          <div className={styles.dots}>
            {events.map(
              (_, index) =>
                index != events.length - 1 &&
                index != events.length - 2 && (
                  <div
                    key={index * 1000}
                    className={classNames(styles.dot, params.slide === index && styles.dot_active)}
                    onClick={() => selectSlide(index)}
                  />
                )
            )}
          </div>
        </section>
      )}
    </>
  );
};
