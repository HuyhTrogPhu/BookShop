package org.example.library.service;

import org.example.library.model.City;

import java.util.List;

public interface CityService {

    List<City> findAllCitesCountry();

    List<City> findAll();


}
