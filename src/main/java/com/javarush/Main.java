package com.javarush;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.City;
import com.javarush.domain.entity.Country;
import com.javarush.repository.CityRepository;
import com.javarush.repository.CountryRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//    private final RedisClient redisClient;

    private final ObjectMapper mapper;

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public Main() {
        cityRepository = new CityRepository();
        countryRepository = new CountryRepository();

//        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
    }

    public static void main(String[] args) {
        Main main = new Main();
        List<City> allCities = main.fetchData(main);
        HibernateUtil.shutdown();
    }

    private List<City> fetchData(Main main) {
        try (Session session = main.sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();

            session.beginTransaction();
            List<Country> countries = main.countryRepository.getAll();

            long totalCount = main.cityRepository.getCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(main.cityRepository.getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }
}