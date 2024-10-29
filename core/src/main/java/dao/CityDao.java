package dao;

import entities.City;
import jakarta.persistence.PersistenceException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.security.auth.RefreshFailedException;
import java.util.List;
import java.util.Optional;

public class CityDao implements Dao<City>
{
    private final static Configuration configuration = createConf();
    @Override
    public void save(City city) {
        try (var fac = configuration.buildSessionFactory(); var session = fac.openSession()) {
            session.beginTransaction();
            session.persist(city);
            session.getTransaction().commit();
        } catch (Exception ex) {
            throw new PersistenceException(ex);
        }
    }
    @Override
    public void update(City city) throws RefreshFailedException {
        try (var fac = configuration.buildSessionFactory(); var session = fac.openSession()) {
            session.beginTransaction();

            session.refresh(city);

            session.getTransaction().commit();
        } catch (Exception ex) {
            throw new RefreshFailedException();
        }
    }
    @Override
    public void delete(Integer id)
     {
         try (var fac = configuration.buildSessionFactory(); var session = fac.openSession()) {
             session.beginTransaction();
             City city = session.get(City.class, id);
             session.remove(city);
             session.getTransaction().commit();
        } catch (Exception ex) {
             // хз че тут писать
        }
     }
     @Override
     public Optional<City> findById(Integer id)
     {
         try (var fac = configuration.buildSessionFactory(); var session = fac.openSession()) {
             return Optional.ofNullable(session.get(City.class, id));
         }
     }
     @Override
     public List<City> findAll()
     {
         try (var fac = configuration.buildSessionFactory(); var session = fac.openSession()) {
             return session.createQuery("from City",City.class).list();
         }
     }
     private static Configuration createConf()
     {
         Configuration configuration = new Configuration();
         configuration.configure("hibernate.cfg.xml");
         configuration.addAnnotatedClass(City.class);
         return configuration;
     }
}