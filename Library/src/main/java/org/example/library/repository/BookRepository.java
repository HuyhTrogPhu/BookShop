package org.example.library.repository;

import org.example.library.model.Book;
import org.example.library.model.Caterogy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByCategory(Caterogy category);


    @Query("select b from Book b ")
    Page<Book> findAllPage(Pageable pageable);

    //Search book by title or author or category
    @Query("select b from Book b join b.category c where b.title like %?1% or b.author like %?1% or c.name like %?1%")
    Page<Book> findByKeyword(String keyword, Pageable pageable);

    @Query("select distinct b.author from Book b")
    List<String> findDistinctAuthor();

    @Query("select b from Book b where b.author = ?1")
    Page<Book> findByAuthor(String author, Pageable pageable);

    @Query("select b from Book b join b.category c where c.name = ?1")
    Page<Book> findByCategoryName(String categoryName, Pageable pageable);

    //Search book like title
    @Query("select  b from Book b where b.title like %?1%")
    Page<Book> searchBook(String keyword, Pageable pageable);

    //Select top 5 book
    List<Book> findTop6ByOrderByCurrentQuantityAsc();
}
