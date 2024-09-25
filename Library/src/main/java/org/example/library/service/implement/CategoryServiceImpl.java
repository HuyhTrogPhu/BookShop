package org.example.library.service.implement;

import org.example.library.model.Caterogy;
import org.example.library.repository.CategoryRepository;
import org.example.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Caterogy add(Caterogy category) {
        return categoryRepository.save(category);
    }


    @Override
    public List<Caterogy> findAllByActivatedTrue() {
        return null;
    }

    @Override
    public List<Caterogy> findALl() {
        return categoryRepository.findAll();
    }

    @Override
    public Caterogy findById(Long id) {
        Optional<Caterogy> category = categoryRepository.findById(id);
        Caterogy foundCaterogy = null;
        if (category.isPresent()) {
            foundCaterogy = category.get();
        } else {
            throw new RuntimeException("Category not found");
        }
        return foundCaterogy;
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void enableById(Long id) {
        Caterogy caterogy = categoryRepository.getById(id);
    }

    @Override
    public Page<Caterogy> pageCategory(Pageable pageable) {
        return categoryRepository.pageCategory(pageable);
    }

    @Override
    public Page<Caterogy> searchCategory(String keyword, Pageable pageable) {
       return categoryRepository.searchCategory(keyword, pageable);
    }
}
