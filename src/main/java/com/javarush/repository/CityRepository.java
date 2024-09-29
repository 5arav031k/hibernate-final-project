package com.javarush.repository;

import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.City;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class CityRepository implements CrudRepository<City, Integer> {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<City> getItems(int offset, int limit) {
        return sessionFactory.getCurrentSession()
                .createQuery("select c from City c", City.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .list();
    }

    @Override
    public long getCount() {
        return Math.toIntExact(sessionFactory.getCurrentSession()
                .createQuery("select count(c) from City c", Long.class)
                .uniqueResult());
    }

    @Override
    public List<City> getAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("select c from City c", City.class)
                .list();
    }

    @Override
    public City getById(final Integer id) {
        return sessionFactory.getCurrentSession()
                .createQuery("select c from City c where c.id=:id", City.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    @Override
    public City save(final City entity) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.beginTransaction();
        currentSession.persist(entity);
        currentSession.getTransaction().commit();
        return entity;
    }

    @Override
    public void deleteById(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.createQuery("delete from City c where c.id=:id")
                .setParameter("id", id)
                .executeUpdate();
        session.getTransaction().commit();
    }
}
