package com.javarush;

import com.google.gson.Gson;
import com.javarush.config.HibernateUtil;
import com.javarush.domain.entity.City;
import com.javarush.domain.entity.Country;
import com.javarush.domain.entity.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.redis.Language;
import com.javarush.repository.CityRepository;
import com.javarush.repository.CountryRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import redis.clients.jedis.JedisPooled;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    private final JedisPooled jedis;
    private final Gson gson;

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public Main() {
        cityRepository = new CityRepository();
        countryRepository = new CountryRepository();
        gson = new Gson();
        jedis = new JedisPooled("localhost", 6379);
    }

    public static void main(String[] args) {
        Main main = new Main();

        List<City> allCities = main.fetchData(main);
        List<CityCountry> preparedData = main.transformData(allCities);
        main.pushToRedis(preparedData);
        main.sessionFactory.getCurrentSession().close();

        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        main.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        main.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

        HibernateUtil.shutdown();
    }

    private List<City> fetchData(Main main) {
        try (Session session = main.sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();

            session.beginTransaction();
            main.countryRepository.getAll();

            long totalCount = main.cityRepository.getCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(main.cityRepository.getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }

    private List<CityCountry> transformData(List<City> cities) {
        return cities.stream().map(city -> {
            CityCountry res = new CityCountry();
            res.setId(city.getId());
            res.setName(city.getName());
            res.setPopulation(city.getPopulation());
            res.setDistrict(city.getDistrict());

            Country country = city.getCountry();
            res.setAlternativeCountryCode(country.getAlternativeCode());
            res.setContinent(country.getContinent());
            res.setCountryCode(country.getCode());
            res.setCountryName(country.getName());
            res.setCountryPopulation(country.getPopulation());
            res.setCountryRegion(country.getRegion());
            res.setCountrySurfaceArea(country.getSurfaceArea());
            Set<CountryLanguage> countryLanguages = country.getLanguages();
            Set<Language> languages = countryLanguages.stream().map(cl -> {
                Language language = new Language();
                language.setLanguage(cl.getLanguage());
                language.setIsOfficial(cl.isOfficial());
                language.setPercentage(cl.getPercentage());
                return language;
            }).collect(Collectors.toSet());
            res.setLanguages(languages);

            return res;
        }).collect(Collectors.toList());
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