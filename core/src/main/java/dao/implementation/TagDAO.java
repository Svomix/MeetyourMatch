package dao.implementation;

import dao.DAO;
import entities.Tag;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagDAO implements DAO<Tag> {
    private final static SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.xml")
            .buildSessionFactory();

    @Override
    public void save(Tag tag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(tag);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Tag tag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.refresh(tag);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Tag tag = session.get(Tag.class, id);
            session.remove(tag);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Tag> findAll() {
        List<Tag> tags = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            tags = session.createQuery("from Tag",Tag.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

    @Override
    public Optional<Tag> findById(Integer id) {
        Optional<Tag> tag = Optional.empty();
        try (Session session = sessionFactory.openSession()) {
            tag = Optional.ofNullable(session.get(Tag.class, id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }
}
