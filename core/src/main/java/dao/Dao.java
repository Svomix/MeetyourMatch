package dao;

import javax.security.auth.RefreshFailedException;
import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface Dao<E>
{
    void save(E e);
    void update(E e) throws RefreshFailedException;
    void delete(Integer id);
    List<E> findAll();
    Optional<E> findById(Integer id);
}
