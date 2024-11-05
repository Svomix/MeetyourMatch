package com.javanostra.spring.core.dao.implementation;

import com.javanostra.spring.core.dao.DAO;
import com.javanostra.spring.core.entities.Chat;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatDAO implements DAO<Chat> {
    private final SessionFactory sessionFactory;

    public ChatDAO() {
        sessionFactory = new Configuration().configure().addAnnotatedClass(Chat.class).buildSessionFactory();
    }

    @Override
    public void save(Chat entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        }
    }

    @Override
    public void update(Chat entity) {
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
            Chat entity = session.get(Chat.class, id);
            if (entity != null) {
                session.remove(entity);
            }
            transaction.commit();
        }
    }

    @Override
    public Chat findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Chat.class, id);
        }
    }

    @Override
    public List<Chat> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Chat", Chat.class).list();
        }
    }
}
