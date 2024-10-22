import Layout from '@components/Layout';
import Map from '@components/Map';
import Search from '@components/Search';
import styles from './page.module.css';

export default function MapPage() {
  return (
    <Layout>
      <div className={styles.wrap}>
        <Search placeholder='поиск по названию или #тегу'/>
        <Map />
      </div>
    </Layout>
  );
}
