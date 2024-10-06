package com.javarush.service.impl;

import com.javarush.cache.RedisRepository;
import com.javarush.converter.DataConverter;
import com.javarush.domain.entity.Country;
import com.javarush.domain.exceptions.EntityNotFoundException;
import com.javarush.redis.CityCountry;
import com.javarush.repository.CountryRepository;
import com.javarush.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final RedisRepository redisRepository;

    @Override
    public Country getCountryById(int countryId) {
        if (countryId <= 0) {
            log.error("Invalid country id: {}", countryId);
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        String name = "getCountryById:" + countryId;

        if (redisRepository.exist(name)) {
            CityCountry cityCountry = redisRepository.get(name, CityCountry.class);
            return DataConverter.convertToCounty(cityCountry);
        } else {
            Country country = countryRepository.getById(countryId);
            if (country == null) {
                log.error("Country with id {} not found", countryId);
                throw new EntityNotFoundException(countryId);
            }

            CityCountry cityCountry = DataConverter.convertToCityCountry(country);
            redisRepository.setIfFrequentlyUsed(name, cityCountry);

            return country;

        }
    }

    @Override
    public Country saveCountry(Country country) {
        if (country == null) {
            log.error("Cannot save county because county is null");
            throw new IllegalArgumentException("Country cannot be null");
        }
        Integer countryId = country.getId();
        if (countryId == null || countryId <= 0) {
            log.error("Invalid country id: {}", countryId);
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        try {
            return countryRepository.save(country);
        } catch (Exception e) {
            log.error("Cannot save county: {}", e.getMessage());
            throw new IllegalArgumentException("Cannot save county");
        }
    }

    @Override
    public List<Country> fetchAllCountries() {
        try {
            return countryRepository.getAll();
        } catch (Exception e) {
            log.error("Cannot fetch all countries");
            throw new IllegalArgumentException("Cannot fetch all countries");
        }
    }

    @Override
    public long fetchCountriesCount() {
        try {
            return countryRepository.getCount();
        } catch (Exception e) {
            log.error("Cannot fetch countries count");
            throw new IllegalArgumentException("Cannot fetch countries count");
        }
    }

    @Override
    public void deleteCountryById(int countryId) {
        if (countryId <= 0) {
            log.error("Invalid country id: {}", countryId);
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        Country country = countryRepository.getById(countryId);
        if (country == null) {
            log.error("Country with id {} not found", countryId);
            throw new EntityNotFoundException(countryId);
        }
        try {
            countryRepository.deleteById(countryId);
        } catch (Exception e) {
            log.error("Cannot delete county: {}", e.getMessage());
            throw new IllegalArgumentException("Cannot delete county");
        }
        log.info("Country with id {} deleted", countryId);
    }
}
