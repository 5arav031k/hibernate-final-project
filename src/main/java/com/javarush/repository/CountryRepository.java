package com.javarush.repository;

import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class CountryRepository implements CrudRepository<Country, Integer> {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public long getCount() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select count(c) from Country c", Long.class)
                    .uniqueResult();
        }
    }

    @Override
    public List<Country> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select c from Country c join fetch c.languages", Country.class)
                    .list();
        }
    }

    @Override
    public Country getById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select c from Country c where c.id = :id", Country.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    @Override
    public Country save(Country entity) {
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
            session.createQuery("delete from Country c where c.id=:id")
                    .setParameter("id", id)
                    .executeUpdate();
            transaction.commit();
        }
    }
}
