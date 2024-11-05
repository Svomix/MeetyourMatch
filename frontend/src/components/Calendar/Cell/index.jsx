import styles from './index.module.css';

export default ({ index, event_list, style, disabled }) => {
  return (
    <div style={style} className={styles.cell}>
      {disabled ? 0 : index}
    </div>
  );
};
