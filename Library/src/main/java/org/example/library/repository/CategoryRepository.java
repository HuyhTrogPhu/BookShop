package org.example.library.repository;

import org.example.library.model.Caterogy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Caterogy, Long> {

    @Query("select c from Caterogy c")
    Page<Caterogy>  pageCategory(Pageable pageable);

    @Query("select  c from Caterogy c where c.name like %?1%")
    Page<Caterogy> searchCategory(String keyword, Pageable pageable);
}
