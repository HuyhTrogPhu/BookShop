package org.example.library.service;


import org.example.library.model.Caterogy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Caterogy add(Caterogy category);

//    Caterogy update(Caterogy category);

    List<Caterogy> findAllByActivatedTrue();

    List<Caterogy> findALl();

    Caterogy findById(Long id);

    void deleteCategory(Long id);

    void enableById(Long id);

//    List<CategoryDto> getCategoriesAndSize();

    Page<Caterogy> pageCategory(Pageable pageable);

    Page<Caterogy> searchCategory(String keyword, Pageable pageable);

}
