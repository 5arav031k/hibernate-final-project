package com.javarush.service;

import com.javarush.domain.entity.Country;

import java.util.List;

public interface CountryService {
    Country getCountryById(int countryId);

    Country saveCountry(Country country);

    List<Country> fetchAllCountries();

    long fetchCountriesCount();

    void deleteCountryById(int countryId);
}
