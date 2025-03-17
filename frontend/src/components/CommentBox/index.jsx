import styles from './index.module.css';
import Comment from './Comment';
import CreateComment from './CreateComment';

// comments = [{comment_id:, author:, text:, ...}, {}, {}, ...]
export default ({ comments, onSubmit, onDelete }) => {
  return (
    <div className={styles.wrapper}>
      <h3 className={styles.title}>Комментарии:</h3>
      <CreateComment onClick={onSubmit} />
      <div className={styles.comments}>
        {comments.map((el) => (
          <Comment key={el.id} comment={el} onDelete={onDelete} />
        ))}
      </div>
    </div>
  );
};
