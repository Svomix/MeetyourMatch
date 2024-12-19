
import React from 'react'
import styles from './index.module.css';

export default function ChristmasLights() {
    return (
        <ul className={styles.strand}>
        {Array.from(Array(40).keys()).map((v) => <li key={v}></li>)}
        </ul>
    )
}
