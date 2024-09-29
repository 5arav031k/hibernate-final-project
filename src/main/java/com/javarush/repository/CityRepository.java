package com.javarush.repository;

import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.City;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CityRepository implements CrudRepository<City, Integer>{

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<City> getItems(int offset, int limit) {
        Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c", City.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public long getCount() {
        Query<Long> query = sessionFactory.getCurrentSession().createQuery("select count(c) from City c", Long.class);
        return Math.toIntExact(query.uniqueResult());
    }

    @Override
    public List<City> getAll() {
        Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c", City.class);
        return query.list();
    }

    @Override
    public City getById(final Integer id) {
        Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c where c.id=:id", City.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    @Override
    public City save(final City entity) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.beginTransaction();
        currentSession.persist(entity);
        currentSession.getTransaction().commit();
        return entity;
    }
}
