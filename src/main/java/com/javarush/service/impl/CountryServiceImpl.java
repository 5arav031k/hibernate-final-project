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
        return countryRepository.save(country);
    }

    @Override
    public List<Country> fetchAllCountries() {
        return countryRepository.getAll();
    }

    @Override
    public long fetchCountriesCount() {
        return countryRepository.getCount();
    }

    @Override
    public void deleteCountryById(int countryId) {
        if (countryRepository.getById(countryId) == null) {
            log.error("Country with id {} not found", countryId);
            throw new EntityNotFoundException(countryId);
        }
        countryRepository.deleteById(countryId);
        log.info("Country with id {} deleted", countryId);
    }
}
