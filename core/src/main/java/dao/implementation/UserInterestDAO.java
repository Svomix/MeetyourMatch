package dao.implementation;

import dao.DAO;
import entities.User;
import entities.UserInterest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserInterestDAO implements DAO<UserInterest> {
    private final static SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.xml")
            .buildSessionFactory();

    @Override
    public void save(UserInterest userInterest) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(userInterest);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UserInterest userInterest) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.refresh(userInterest);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            UserInterest userInterest = session.get(UserInterest.class, id);
            session.remove(userInterest);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UserInterest> findAll() {
        List<UserInterest> userInterests = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            userInterests = session.createQuery("from UserInterest ",UserInterest.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInterests;
    }

    @Override
    public Optional<UserInterest> findById(Integer id) {
        Optional<UserInterest> userInterest = Optional.empty();
        try (Session session = sessionFactory.openSession()) {
            userInterest = Optional.ofNullable(session.get(UserInterest.class, id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInterest;
    }
}
