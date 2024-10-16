package com.javarush.converter;

import com.javarush.domain.entity.City;
import com.javarush.domain.entity.Country;
import com.javarush.domain.entity.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.redis.Language;

import java.util.Set;
import java.util.stream.Collectors;

public class DataConverter {

    public static CityCountry convertToCityCountry(City city) {
        CityCountry res = convertToCityCountry(city.getCountry());
        res.setId(city.getId());
        res.setName(city.getName());
        res.setPopulation(city.getPopulation());
        res.setDistrict(city.getDistrict());
        return res;
    }

    public static CityCountry convertToCityCountry(Country country) {
        CityCountry res = new CityCountry();
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
    }

    public static City convertToCity(CityCountry cityCountry) {
        City city = new City();
        city.setId(cityCountry.getId());
        city.setName(cityCountry.getName());
        city.setPopulation(cityCountry.getPopulation());
        city.setDistrict(cityCountry.getDistrict());
        return city;
    }

    public static Country convertToCounty(CityCountry cityCountry) {
        Country country = new Country();
        country.setCode(cityCountry.getCountryCode());
        country.setName(cityCountry.getCountryName());
        country.setContinent(cityCountry.getContinent());
        country.setRegion(cityCountry.getCountryRegion());
        country.setSurfaceArea(cityCountry.getCountrySurfaceArea());
        country.setPopulation(cityCountry.getCountryPopulation());
        country.setAlternativeCode(cityCountry.getAlternativeCountryCode());
        Set<Language> languages = cityCountry.getLanguages();
        Set<CountryLanguage> countryLanguages = languages.stream().map(l -> {
            CountryLanguage countryLanguage = new CountryLanguage();
            countryLanguage.setLanguage(l.getLanguage());
            countryLanguage.setOfficial(l.getIsOfficial());
            countryLanguage.setPercentage(l.getPercentage());
            return countryLanguage;
        }).collect(Collectors.toSet());
        country.setLanguages(countryLanguages);
        return country;
    }
}
