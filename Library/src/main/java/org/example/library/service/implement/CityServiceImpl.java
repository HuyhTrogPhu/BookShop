package org.example.library.service.implement;

import org.example.library.model.City;
import org.example.library.repository.CityRepository;
import org.example.library.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public List<City> findAllCitesCountry() {
        return cityRepository.findAllCitiesCountry();
    }

    @Override
    public List<City> findAll() {
        return cityRepository.findAll();
    }

}
