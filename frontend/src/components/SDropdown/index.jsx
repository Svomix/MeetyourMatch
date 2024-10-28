'use client'
import { useRef, useState } from 'react';
import styles from './index.module.css';
import classNames from '@/utils/classnames';

export default function SDropdown({data, placeholder, onSelect, className}) {

  let [Sel, setSel] = useState(data[0].key)

  const input = useRef();
  const data_wrap = useRef();

  function onFocus(){
    data_wrap.current?.classList.add(styles.block)
    data_wrap.current?.classList.remove(styles.invisible)
    input.current.placeholder = ''
    input.current.value = ''; 
  }

  function onBlur(){
      data_wrap.current?.classList.add(styles.invisible)
      data_wrap.current?.classList.remove(styles.block)
      input.current.placeholder = placeholder
  }
  function onClick(e){
    input.current.value = e.text;
  }

  return (
    <div className={styles.dropdown_wrap}>
      <div className={classNames(styles.dropdown, className)}>
        <button className={styles.input_wrap} onClick={() => input.current?.focus()}>
          <input placeholder={placeholder} ref={input} className={styles.input} type="text" onFocus={onFocus} onBlur={onBlur}></input>
        </button>
        <span ref={data_wrap} className={classNames(styles.data_wrap, styles.invisible)}>
          {data.map((row) => (
            <button onMouseDown={() => onClick(row)} key={row.key} className={styles.data}>{row.text}</button>
          ))}
        </span>
      </div>
    </div>
  )
}
