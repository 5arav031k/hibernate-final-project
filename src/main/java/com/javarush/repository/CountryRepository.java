package com.javarush.repository;

import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class CountryRepository implements CrudRepository<Country, Integer> {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public long getCount() {
        return sessionFactory.getCurrentSession()
                .createQuery("select count(c) from Country c", Long.class)
                .uniqueResult();
    }

    @Override
    public List<Country> getAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("select c from Country c join fetch c.languages", Country.class)
                .list();
    }

    @Override
    public Country getById(Integer id) {
        return sessionFactory.getCurrentSession()
                .createQuery("select c from Country c where c.id = :id", Country.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    @Override
    public Country save(Country entity) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();
        return entity;
    }

    @Override
    public void deleteById(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.createQuery("delete from Country c where c.id=:id")
                .setParameter("id", id)
                .executeUpdate();
        session.getTransaction().commit();
    }
}
