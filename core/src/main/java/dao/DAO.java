package dao;

import javax.security.auth.RefreshFailedException;
import java.util.List;
import java.util.Optional;

public interface DAO<E>
{
    void save(E e);
    void update(E e);
    void delete(Integer id);
    List<E> findAll();
    Optional<E> findById(Integer id);
}
