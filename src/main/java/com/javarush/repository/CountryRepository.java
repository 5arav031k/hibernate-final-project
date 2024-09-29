package com.javarush.repository;

import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

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
        return sessionFactory.getCurrentSession().createQuery("select c from Country c join fetch c.languages", Country.class).list();
    }

    @Override
    public Country getById(Integer id) {
        Query<Country> query = sessionFactory.getCurrentSession().createQuery("select c from Country c where c.id = :id", Country.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    @Override
    public Country save(Country entity) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();
        return entity;
    }
}
