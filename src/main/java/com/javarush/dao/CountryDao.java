package com.javarush.dao;

import com.javarush.domain.Country;
import org.hibernate.SessionFactory;

import java.util.List;

public class CountryDao {

    private final SessionFactory sessionFactory;

    public CountryDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Country> getAll() {
        return sessionFactory.getCurrentSession().createQuery("select c from Country c", Country.class).list();
    }
}
