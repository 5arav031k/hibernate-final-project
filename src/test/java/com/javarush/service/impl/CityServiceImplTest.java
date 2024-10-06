package com.javarush.service.impl;

import com.javarush.cache.RedisRepository;
import com.javarush.converter.DataConverter;
import com.javarush.domain.entity.City;
import com.javarush.domain.entity.Country;
import com.javarush.domain.exceptions.EntityNotFoundException;
import com.javarush.redis.CityCountry;
import com.javarush.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceImplTest {

    private CityServiceImpl cityService;
    @Mock
    private RedisRepository redisRepository;
    @Mock
    private CityRepository cityRepository;

    @BeforeEach
    void setUp() {
        cityService = new CityServiceImpl(cityRepository, redisRepository);
    }

    @Test
    public void shouldReturnCityByIdFromRedis() {
        int testId = 1;
        String testName = "getCityById:" + testId;
        CityCountry testCityCountry = DataConverter.convertToCityCountry(getTestCity(testId));

        when(redisRepository.exist(testName)).thenReturn(true);
        when(redisRepository.get(testName, CityCountry.class)).thenReturn(testCityCountry);

        City actualResult = cityService.getCityById(testId);
        City expectedResult = DataConverter.convertToCity(testCityCountry);

        verify(redisRepository).get(testName, CityCountry.class);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldPutCityByIdIntoRedis() {
        int testId = 1;
        String testName = "getCityById:" + testId;
        City testCity = getTestCity(testId);

        when(redisRepository.exist(testName)).thenReturn(false);
        when(cityRepository.getById(testId)).thenReturn(testCity);

        City actualResult = cityService.getCityById(testId);

        verify(redisRepository).setIfFrequentlyUsed(testName, DataConverter.convertToCityCountry(testCity));
        assertEquals(testCity, actualResult);
    }

    @Test
    public void shouldThrowExceptionWhenGetCityByIdWithInvalidParams() {
        int invalidCityId = -1;
        assertThrows(IllegalArgumentException.class, () -> cityService.getCityById(invalidCityId));
    }

    @Test
    public void shouldThrowExceptionWhenSaveCityWithInvalidParams1() {
        assertThrows(IllegalArgumentException.class, () -> cityService.saveCity(null));
    }

    @Test
    public void shouldThrowExceptionWhenSaveCityWithInvalidParams2() {
        assertThrows(IllegalArgumentException.class, () -> cityService.saveCity(new City()));
    }

    @Test
    public void shouldThrowExceptionWhenDeleteCityByIdWithInvalidParams1() {
        int invalidCityId = -1;
        assertThrows(IllegalArgumentException.class, () -> cityService.deleteCityById(invalidCityId));
    }

    @Test
    public void shouldThrowExceptionWhenDeleteCityByIdWithInvalidParams2() {
        int cityId = 1;
        when(cityRepository.getById(cityId)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> cityService.deleteCityById(cityId));
    }

    private static City getTestCity(int testId) {
        City testCity = new City();
        testCity.setId(testId);
        testCity.setName("testName");
        testCity.setPopulation(100000);
        testCity.setDistrict("testDistrict");

        Country testCountry = new Country();
        testCountry.setLanguages(new HashSet<>());
        testCity.setCountry(testCountry);
        return testCity;
    }
}