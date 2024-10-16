package com.javarush.service;

import com.javarush.domain.entity.City;

import java.util.List;

public interface CityService {
    City getCityById(int cityId);

    City saveCity(City city);

    List<City> fetchAllCities();

    long fetchCitiesCount();

    void deleteCityById(int cityId);
}
