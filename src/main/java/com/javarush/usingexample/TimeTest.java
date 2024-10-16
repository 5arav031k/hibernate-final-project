package com.javarush.usingexample;

import com.google.gson.Gson;
import com.javarush.config.HibernateUtil;
import com.javarush.config.RedisConfig;
import com.javarush.converter.DataConverter;
import com.javarush.domain.entity.City;
import com.javarush.domain.entity.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.repository.CityRepository;
import com.javarush.repository.CountryRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import redis.clients.jedis.UnifiedJedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TimeTest {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    private final Gson gson;

    private final UnifiedJedis jedis = RedisConfig.getUnifiedJedis();
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public TimeTest() {
        cityRepository = new CityRepository();
        countryRepository = new CountryRepository();
        gson = new Gson();
    }

    public void execute() {
        List<City> allCities = fetchData();
        List<CityCountry> preparedData = transformData(allCities);
        pushToRedis(preparedData);
        sessionFactory.getCurrentSession().close();

        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));
    }

    private List<City> fetchData() {
        try (Session session = sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();

            Transaction transaction = session.beginTransaction();
            countryRepository.getAll();

            long totalCount = cityRepository.getCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(cityRepository.getItems(i, step));
            }
            transaction.commit();
            return allCities;
        }
    }

    private List<CityCountry> transformData(List<City> cities) {
        return cities.stream().map(DataConverter::convertToCityCountry).collect(Collectors.toList());
    }

    private void pushToRedis(List<CityCountry> data) {
        for (CityCountry cityCountry : data) {
            jedis.set(String.valueOf(cityCountry.getId()), gson.toJson(cityCountry));
        }
    }

    private void testRedisData(List<Integer> ids) {
        for (Integer id : ids) {
            String value = jedis.get(String.valueOf(id));
            gson.fromJson(value, CityCountry.class);
        }
    }

    private void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityRepository.getById(id);
                Set<CountryLanguage> ignored = city.getCountry().getLanguages();
            }
            session.getTransaction().commit();
        }
    }
}
