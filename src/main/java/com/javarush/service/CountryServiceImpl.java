package com.javarush.service;

import com.javarush.domain.entity.Country;
import com.javarush.domain.exceptions.EntityNotFoundException;
import com.javarush.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    public Country getCountryById(int countryId) {
        Country city = countryRepository.getById(countryId);
        if (city == null) {
            log.error("Country with id {} not found", countryId);
            throw new EntityNotFoundException(countryId);
        }
        return city;
    }

    @Override
    public Country saveCountry(Country country) {
        if (country == null) {
            log.error("Cannot save county because county is null");
            throw new IllegalArgumentException("Country cannot be null");
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
        return countryRepository.getAll();
    }

    @Override
    public long fetchCountriesCount() {
        return countryRepository.getCount();
    }

    @Override
    public void deleteCountryById(int countryId) {
        Country country = countryRepository.getById(countryId);
        if (country == null) {
            log.error("Country with id {} not found", countryId);
            throw new EntityNotFoundException(countryId);
        }
        countryRepository.deleteById(countryId);
    }
}
