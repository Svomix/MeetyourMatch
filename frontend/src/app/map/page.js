import Layout from '@components/Layout';
import Map from '@components/Map';
import SDropdown from '@components/SDropdown';
import Search from '@components/Search';
import styles from './page.module.css';

export default function MapPage() {
  return (
    <Layout>
      <div className={styles.wrap}>
        <section className={styles.controls}>
          <Search placeholder="Поиск" />
          <SDropdown
            placeholder={'Платно?'}
            data={[
              { key: 'pay', text: 'Платно' },
              { key: 'free', text: 'Бесплатно' }
            ]}
            className={styles.dbar}
          />
          <SDropdown
            placeholder={'На этой неделе'}
            data={[
              { key: 'week', text: 'На этой неделе' },
              { key: 'week2', text: 'В течении 2-х недель' }
            ]}
            className={styles.dbar}
          />
          <SDropdown
            placeholder={'Теги'}
            data={[
              { key: 'week', text: 'Ы' },
              { key: 'week2', text: 'ЫЫ' }
            ]}
          />
        </section>
        <Map />
      </div>
    </Layout>
  );
}
