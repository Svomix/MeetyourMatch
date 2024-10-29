package dao.implementation;

import dao.DAO;
import entities.City;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CityDAO implements DAO<City>
{
    private final static SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.xml")
            .buildSessionFactory();


    @Override
    public void save(City city) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(city);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void update(City city) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.refresh(city);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete(Integer id)
     {
         try (Session session = sessionFactory.openSession()) {
             session.beginTransaction();
             City city = session.get(City.class, id);
             session.remove(city);
             session.getTransaction().commit();
        } catch (Exception e) {
             e.printStackTrace();
        }
     }
     @Override
     public Optional<City> findById(Integer id)
     {
         Optional<City> city = Optional.empty();
         try (Session session = sessionFactory.openSession()) {
             city = Optional.ofNullable(session.get(City.class, id));
         } catch (Exception e) {
             e.printStackTrace();
         }
         return city;
     }
     @Override
     public List<City> findAll()
     {
         List<City> cities = new ArrayList<>();
         try (Session session = sessionFactory.openSession()) {
             cities = session.createQuery("from City",City.class).list();
         } catch (Exception e) {
             e.printStackTrace();
         }
         return cities;
     }
}