package org.example.library.repository;

import org.example.library.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    @Query("select city from City city join fetch city.country")
    List<City> findAllCitiesCountry();


}
