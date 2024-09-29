package com.javarush.service;

import com.javarush.domain.entity.City;
import com.javarush.domain.exceptions.EntityNotFoundException;
import com.javarush.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public City getCityById(int cityId) {
        City city = cityRepository.getById(cityId);
        if (city == null) {
            log.error("City with id {} not found", cityId);
            throw new EntityNotFoundException(cityId);
        }
        return city;
    }

    @Override
    public City saveCity(City city) {
        if (city == null) {
            log.error("Cannot save city because city is null");
            throw new IllegalArgumentException("City cannot be null");
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
        return cityRepository.getAll();
    }

    @Override
    public long fetchCitiesCount() {
        return cityRepository.getCount();
    }

    @Override
    public void deleteCityById(int cityId) {
        if (cityRepository.getById(cityId) == null) {
            log.error("City with id {} not found", cityId);
            throw new EntityNotFoundException(cityId);
        }
        cityRepository.deleteById(cityId);
    }
}
