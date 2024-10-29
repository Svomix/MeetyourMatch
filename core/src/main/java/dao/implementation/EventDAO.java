package dao.implementation;

import dao.DAO;
import entities.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventDAO implements DAO<Event> {

    private final static SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.xml")
            .buildSessionFactory();

    @Override
    public void save(Event event) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(event);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Event event) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.refresh(event);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Event event = session.get(Event.class, id);
            session.remove(event);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            events = session.createQuery("from Event",Event.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Optional<Event> findById(Integer id) {
        Optional<Event> event = Optional.empty();
        try (Session session = sessionFactory.openSession()) {
            event = Optional.ofNullable(session.get(Event.class, id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }
}
