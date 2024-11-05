package com.javanostra.spring.core.dao.implementation;

import com.javanostra.spring.core.dao.DAO;
import com.javanostra.spring.core.entities.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class EventDAO implements DAO<Event> {
    private final SessionFactory sessionFactory;

    public EventDAO() {
        sessionFactory = new Configuration().configure().addAnnotatedClass(Event.class).buildSessionFactory();
    }

    @Override
    public void save(Event entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        }
    }

    @Override
    public void update(Event entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        }
    }

    @Override
    public void delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Event entity = session.get(Event.class, id);
            if (entity != null) {
                session.remove(entity);
            }
            transaction.commit();
        }
    }

    @Override
    public Event findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Event.class, id);
        }
    }

    @Override
    public List<Event> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Event", Event.class).list();
        }
    }
}
