import Link from "next/link";
import styles from "./index.module.css"

export default function MapEventCard({data}) {
    return <Link className={styles.card} href={`/events/${data.id}`}>
        <div className={styles.container}>
            <p className={styles.title}>{data.title}</p>
            <p className={styles.date}>{new Date(data.date || 0).toLocaleString('ru-RU')}</p>
        </div>
    </Link>;
}
