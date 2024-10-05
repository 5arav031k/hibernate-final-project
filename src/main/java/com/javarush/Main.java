package com.javarush;

import com.javarush.cache.RedisRepository;
import com.javarush.config.HibernateUtil;
import com.javarush.repository.CityRepository;
import com.javarush.repository.CountryRepository;
import com.javarush.service.impl.CityServiceImpl;
import com.javarush.service.impl.CountryServiceImpl;
import com.javarush.usingexample.TimeTest;

public class Main {

    public static void main(String[] args) {
        TimeTest timeTest = new TimeTest();
        timeTest.execute();

        CityServiceImpl cityService = new CityServiceImpl(new CityRepository(), new RedisRepository());
        for (int i = 0; i < 15; i++) {
            System.out.println("i: " + i);
            cityService.getCityById(10);
        }

        CountryServiceImpl countryService = new CountryServiceImpl(new CountryRepository(), new RedisRepository());
        for (int i = 0; i < 15; i++) {
            System.out.println("i: " + i);
            countryService.getCountryById(10);
        }

        HibernateUtil.shutdown();
    }
}