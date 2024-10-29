package dao.implementation;

import dao.DAO;
import entities.EventTag;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventTagDAO implements DAO<EventTag> {
    private final static SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.xml")
            .buildSessionFactory();

    @Override
    public void save(EventTag eventTag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(eventTag);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(EventTag eventTag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.refresh(eventTag);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            EventTag eventTag = session.get(EventTag.class, id);
            session.remove(eventTag);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<EventTag> findAll() {
        List<EventTag> eventTags = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            eventTags = session.createQuery("from EventTag",EventTag.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventTags;
    }

    @Override
    public Optional<EventTag> findById(Integer id) {
        Optional<EventTag> eventTag = Optional.empty();
        try (Session session = sessionFactory.openSession()) {
            eventTag = Optional.ofNullable(session.get(EventTag.class, id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventTag;
    }
}
