package com.javarush.service.impl;

import com.javarush.cache.RedisRepository;
import com.javarush.converter.DataConverter;
import com.javarush.domain.entity.Country;
import com.javarush.domain.enums.Continent;
import com.javarush.domain.exceptions.EntityNotFoundException;
import com.javarush.redis.CityCountry;
import com.javarush.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

    private CountryServiceImpl countryService;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private RedisRepository redisRepository;

    @BeforeEach
    void setUp() {
        countryService = new CountryServiceImpl(countryRepository, redisRepository);
    }

    @Test
    public void shouldReturnCountryByIdFromRedis() {
        String testName = "getCountryById:1";
        CityCountry testCityCountry = getTestCityCountry();

        when(redisRepository.exist(testName)).thenReturn(true);
        when(redisRepository.get(testName, CityCountry.class)).thenReturn(testCityCountry);

        Country actualResult = countryService.getCountryById(1);
        Country expectedResult = DataConverter.convertToCounty(testCityCountry);

        verify(redisRepository).get(testName, CityCountry.class);
        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void shouldPutCountryByIdIntoRedis() {
        int testId = 1;
        String testName = "getCountryById:" + testId;
        Country testCountry = DataConverter.convertToCounty(getTestCityCountry());

        when(redisRepository.exist(testName)).thenReturn(false);
        when(countryRepository.getById(testId)).thenReturn(testCountry);

        Country actualResult = countryService.getCountryById(testId);

        verify(redisRepository).setIfFrequentlyUsed(testName, getTestCityCountry());
        assertEquals(testCountry, actualResult);
    }

    @Test
    public void shouldThrowExceptionWhenGetCityByIdWithInvalidParams() {
        int invalidCountryId = -1;
        assertThrows(IllegalArgumentException.class, () -> countryService.getCountryById(invalidCountryId));
    }

    @Test
    public void shouldThrowExceptionWhenSaveCityWithInvalidParams1() {
        assertThrows(IllegalArgumentException.class, () -> countryService.saveCountry(null));
    }

    @Test
    public void shouldThrowExceptionWhenSaveCityWithInvalidParams2() {
        assertThrows(IllegalArgumentException.class, () -> countryService.saveCountry(new Country()));
    }

    @Test
    public void shouldThrowExceptionWhenDeleteCityByIdWithInvalidParams1() {
        int invalidCountryId = -1;
        assertThrows(IllegalArgumentException.class, () -> countryService.deleteCountryById(invalidCountryId));
    }

    @Test
    public void shouldThrowExceptionWhenDeleteCityByIdWithInvalidParams2() {
        int countryId = 1;
        when(countryRepository.getById(countryId)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> countryService.deleteCountryById(countryId));
    }

    private static CityCountry getTestCityCountry() {
        CityCountry testCityCountry = new CityCountry();
        testCityCountry.setAlternativeCountryCode("testAlternativeCountryCode");
        testCityCountry.setContinent(Continent.EUROPE);
        testCityCountry.setCountryCode("testCountryCode");
        testCityCountry.setCountryName("testCountryName");
        testCityCountry.setCountryPopulation(100000);
        testCityCountry.setCountryRegion("testCountryRegion");
        testCityCountry.setCountrySurfaceArea(new BigDecimal(2));
        testCityCountry.setLanguages(new HashSet<>());
        return testCityCountry;
    }
}