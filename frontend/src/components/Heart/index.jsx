'use client';
import classNames from '@/utils/classnames';
import { useState } from 'react';
import styles from './index.module.css';

export default ({ width, height, className }) => {
  const [fill, setFill] = useState(false);

  const heart_click = (e) => {
    setFill((fill) => !fill);
    e.preventDefault();
  };

  return (
    <svg
      onClick={heart_click}
      className={classNames(className, styles.icon, fill && styles.active)}
      width={width || 20}
      height={height || 20}
      viewBox="0 -1 26 24"
    >
      <path
        fill-rule="evenodd"
        clip-rule="evenodd"
        d="M12.9476 21.4473C6.65888 16.5508 2.96701 13.0385 1.87193 10.9102C0.318814 7.89182 1.06246 4.89043 2.37464 3.24923C3.71899 1.5678 5.30995 1.00388 7.02621 1.00388C9.39647 1.00388 11.3873 2.27103 12.9988 4.80533C14.6106 2.26844 16.6023 1 18.9738 1C20.69 1 22.281 1.56392 23.6254 3.24535C24.9375 4.88655 25.6812 7.88794 24.1281 10.9063C23.033 13.0346 19.3062 16.5482 12.9476 21.4473Z"
        stroke-linecap="square"
        stroke-linejoin="round"
      ></path>
    </svg>
  );
};
