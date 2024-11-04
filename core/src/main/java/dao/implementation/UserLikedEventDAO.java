package dao.implementation;

import dao.DAO;
import entities.User;
import entities.UserLikedEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserLikedEventDAO implements DAO<UserLikedEvent> {
    private final static SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.xml")
            .buildSessionFactory();


    @Override
    public void save(UserLikedEvent userLikedEvent) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(userLikedEvent);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UserLikedEvent userLikedEvent) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.refresh(userLikedEvent);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            UserLikedEvent userLikedEvent = session.get(UserLikedEvent.class, id);
            session.remove(userLikedEvent);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UserLikedEvent> findAll() {
        List<UserLikedEvent> userLikedEvents = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            userLikedEvents = session.createQuery("from UserLikedEvent",UserLikedEvent.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userLikedEvents;
    }

    @Override
    public Optional<UserLikedEvent> findById(Integer id) {
        Optional<UserLikedEvent> userLikedEvent = Optional.empty();
        try (Session session = sessionFactory.openSession()) {
            userLikedEvent = Optional.ofNullable(session.get(UserLikedEvent.class, id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userLikedEvent;
    }
}
