package com.javarush.service.impl;

import com.javarush.cache.RedisRepository;
import com.javarush.converter.DataConverter;
import com.javarush.domain.entity.City;
import com.javarush.domain.exceptions.EntityNotFoundException;
import com.javarush.redis.CityCountry;
import com.javarush.repository.CityRepository;
import com.javarush.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final RedisRepository redisRepository;

    @Override
    public City getCityById(int cityId) {
        if (cityId <= 0) {
            log.error("Invalid city id: {}", cityId);
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        String name = "getCityById:" + cityId;

        if (redisRepository.exist(name)) {
            CityCountry cityCountry = redisRepository.get(name, CityCountry.class);
            return DataConverter.convertToCity(cityCountry);
        } else {
            City city = cityRepository.getById(cityId);
            if (city == null) {
                log.error("City with id {} not found", cityId);
                throw new EntityNotFoundException(cityId);
            }

            CityCountry cityCountry = DataConverter.convertToCityCountry(city);
            redisRepository.setIfFrequentlyUsed(name, cityCountry);

            return city;
        }
    }

    @Override
    public City saveCity(City city) {
        if (city == null) {
            log.error("Cannot save city because city is null");
            throw new IllegalArgumentException("City cannot be null");
        }
        Integer cityId = city.getId();
        if (cityId == null || cityId <= 0) {
            log.error("Invalid city id: {}", cityId);
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        try {
            return cityRepository.save(city);
        } catch (Exception e) {
            log.error("Cannot save city: {}", e.getMessage());
            throw new IllegalArgumentException("Cannot save city");
        }
    }

    @Override
    public List<City> fetchAllCities() {
        try {
            return cityRepository.getAll();
        } catch (Exception e) {
            log.error("Cannot fetch all cities: {}", e.getMessage());
            throw new IllegalArgumentException("Cannot fetch all cities");
        }
    }

    @Override
    public long fetchCitiesCount() {
        try {
            return cityRepository.getCount();
        } catch (Exception e) {
            log.error("Cannot fetch cities count: {}", e.getMessage());
            throw new IllegalArgumentException("Cannot fetch cities count");
        }
    }

    @Override
    public void deleteCityById(int cityId) {
        if (cityId <= 0) {
            log.error("Invalid city id: {}", cityId);
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (cityRepository.getById(cityId) == null) {
            log.error("City with id {} not found", cityId);
            throw new EntityNotFoundException(cityId);
        }
        try {
            cityRepository.deleteById(cityId);
        } catch (Exception e) {
            log.error("Cannot delete city: {}", e.getMessage());
            throw new IllegalArgumentException("Cannot delete city");
        }
        log.info("City with id {} deleted", cityId);
    }
}
