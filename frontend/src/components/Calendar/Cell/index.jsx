import Event from '../Event';
import styles from './index.module.css';

export default ({ index, event_list, style, disabled }) => {
  return (
    <div style={style} className={styles.cell}>
      {disabled ? 0 : index}
      {event_list?.map((el) => (
        <Event key={el.event.id} event={el} />
      ))}
    </div>
  );
};
