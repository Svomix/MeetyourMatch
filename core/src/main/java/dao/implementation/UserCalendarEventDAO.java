package dao.implementation;

import dao.DAO;
import entities.User;
import entities.UserCalendarEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserCalendarEventDAO implements DAO<UserCalendarEvent> {
    private final static SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.xml")
            .buildSessionFactory();

    @Override
    public void save(UserCalendarEvent userCalendarEvent) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(userCalendarEvent);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UserCalendarEvent userCalendarEvent) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.refresh(userCalendarEvent);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            UserCalendarEvent userCalendarEvent = session.get(UserCalendarEvent.class, id);
            session.remove(userCalendarEvent);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UserCalendarEvent> findAll() {
        List<UserCalendarEvent> userCalendarEvents = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            userCalendarEvents = session.createQuery("from UserCalendarEvent ",UserCalendarEvent.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userCalendarEvents;
    }

    @Override
    public Optional<UserCalendarEvent> findById(Integer id) {
        Optional<UserCalendarEvent> userCalendarEvent = Optional.empty();
        try (Session session = sessionFactory.openSession()) {
            userCalendarEvent = Optional.ofNullable(session.get(UserCalendarEvent.class, id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userCalendarEvent;
    }
}
