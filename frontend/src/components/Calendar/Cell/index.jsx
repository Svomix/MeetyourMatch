import Event from '../Event';
import styles from './index.module.css';

export default ({ index, event_list, style, disabled }) => {
  return (
    <div style={style} className={styles.cell}>
      {disabled ? 0 : index}
      {event_list.map((el) => (Math.random() < 0.1 ? <Event>{el}</Event> : ''))}
    </div>
  );
};
