package com.javarush.repository;

import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.City;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class CityRepository implements CrudRepository<City, Integer> {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<City> getItems(int offset, int limit) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select c from City c", City.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
        }
    }

    @Override
    public long getCount() {
        try (Session session = sessionFactory.openSession()) {
            return Math.toIntExact(session.createQuery("select count(c) from City c", Long.class)
                    .uniqueResult());
        }
    }

    @Override
    public List<City> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select c from City c", City.class)
                    .list();
        }
    }

    @Override
    public City getById(final Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select c from City c join fetch c.country where c.id=:id", City.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    @Override
    public City save(final City entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return entity;
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery("delete from City c where c.id=:id")
                    .setParameter("id", id)
                    .executeUpdate();
            transaction.commit();
        }
    }
}
