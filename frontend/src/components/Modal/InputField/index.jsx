import styles from './index.module.css';

export default function InputField({ name, title, ...rest }) {
  return (
    <div className={styles.container}>
      {/* <p>{title}</p> */}
      <div className={styles.wrap}>
        <input name={name} {...rest} className={styles.input} />
      </div>
    </div>
  );
}
