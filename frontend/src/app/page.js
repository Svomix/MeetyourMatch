import Layout from '@/components/Layout';
import styles from './page.module.css';

export default function Home() {
  return (
    <Layout>
      <h1 className={styles.title}>Найдите события, хобби, или компанию единомышленников</h1>
    </Layout>
  );
}
