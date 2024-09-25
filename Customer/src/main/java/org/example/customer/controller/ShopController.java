package org.example.customer.controller;

import jakarta.servlet.http.HttpSession;
import org.example.library.dto.BookDto;
import org.example.library.model.Book;
import org.example.library.model.Caterogy;
import org.example.library.repository.BookRepository;
import org.example.library.repository.CategoryRepository;
import org.example.library.service.BookService;
import org.example.library.service.CategoryService;
import org.example.library.service.implement.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ShopController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    HttpSession session;

    private static final int pageSize = 10;
    @Autowired
    private BookServiceImpl bookServiceImpl;


    @Autowired
    private ConversionService conversionService;


    private void loadAction(Model model){
        List<Caterogy> caterogyList = categoryRepository.findAll();
        model.addAttribute("categoryList", caterogyList);

        List<String> authorBook = bookRepository.findDistinctAuthor();
        model.addAttribute("authorBook", authorBook);
    }

    @RequestMapping(value = "/shop", method = RequestMethod.GET)
    public String getShop(@RequestParam(defaultValue = "0") int page, Model model) {

        loadAction(model);

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Book> bookPage = bookRepository.findAllPage(pageable);
        model.addAttribute("bookList", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        return "shop";
    }

    //    Views detail
    @GetMapping("/viewsDetail")
    public String getDetailShop(@RequestParam("bookId") Long id, Model model) {

        try {
            Book bookFound = bookRepository.findById(id).orElse(null);
            if (bookFound != null) {
                BookDto bookDetail = bookService.convertToBookDto(bookFound);
                model.addAttribute("bookDetail", bookDetail);
            } else {
                return "redirect:/shop";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/shop";
        }


        return "detail";
    }

    //    Find by category name
    @PostMapping("/findByCategoryName")
    public String findByCategoryName(@RequestParam(value = "categoryName", required = false) String categoryName,
                                     @RequestParam(defaultValue = "0") int page, Model model) {
        try {
            if (categoryName.isEmpty()) {
                categoryName = (String) session.getAttribute("categoryName");
            } else {
                session.setAttribute("categoryName", categoryName);
            }
            if (categoryName.isEmpty()) {
                return "redirect:/shop";
            }
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Book> bookPage = bookService.findByCategoryName(categoryName, pageable);


            model.addAttribute("bookList", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("categoryName", categoryName);

            loadAction(model);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "shop";
    }


    @PostMapping("/searchByKeyword")
    public String searchByKeyword(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page, Model model) {
        try {
            if (keyword == null || keyword.isEmpty()) {
                keyword = (String) session.getAttribute("keyword");
            } else {
                session.setAttribute("keyword", keyword);
            }

            if (keyword.isEmpty()) {
                return "redirect:/shop";
            }
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Book> bookPage = bookService.searchBooks(keyword, pageable);
            model.addAttribute("bookList", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("keyword", keyword);

            loadAction(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "shop";
    }

//    Find by author

    @PostMapping("/findByAuthor")
    public String findByAuthor(@RequestParam(value = "author", required = false) String author,
                               @RequestParam(defaultValue = "0") int page, Model model) {

        try {
            if (author.isEmpty()) {
                author = (String) session.getAttribute("author");
            } else {
                session.setAttribute("author", author);
            }

            if (author.isEmpty()) {
                return "redirect:/shop";
            }

            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Book> bookPage = bookService.findByAuthor(author, pageable);

            model.addAttribute("bookList", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("author", author);

           loadAction(model);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return "shop";
    }

//    Sort from  A to Z
    @PostMapping("/sortFromAToZ")
    public String sortFromAToZ(@RequestParam(defaultValue = "0") int page, Model model) {
        try {

            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "title"));
            Page<Book> bookPage = bookRepository.findAllPage(pageable);

            model.addAttribute("bookList", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());


            loadAction(model);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/shop";
        }
        return "shop";

    }

//    Get sort from A to Z
//    @GetMapping("/sortFromAToZ")
//    public String getSortFromAToZ(@RequestParam(defaultValue = "0") int page, Model model){
//
//        try {
//            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "title"));
//            Page<Book> bookPage = bookRepository.findAllPage(pageable);
//
//            model.addAttribute("bookList", bookPage.getContent());
//            model.addAttribute("currentPage", page);
//            model.addAttribute("totalPages", bookPage.getTotalPages());
//            model.addAttribute("sort", "title");
//
//            loadAction(model);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "redirect:/shop";
//        }
//
//        return "shop";
//    }


//    Sort from Z to A
    @PostMapping("/sortFromZToA")
    public String sortFromZToA(@RequestParam(defaultValue = "0") int page, Model model ){

        try {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "title"));
            Page<Book> bookPage = bookRepository.findAllPage(pageable);

            model.addAttribute("bookList", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());

            loadAction(model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/shop";
        }

        return "shop";
    }


//    Sort from low to high
    @PostMapping("/sortLowToHigh")
    public String sortLowToHigh(@RequestParam(defaultValue = "0") int page, Model model){

        try {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "costPrice"));
            Page<Book> bookPage = bookRepository.findAllPage(pageable);

            model.addAttribute("bookList", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());

            loadAction(model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/shop";
        }

        return "shop";
    }


//    Sort from high to low
    @PostMapping("/sortHighToLow")
    public String sortFromHighToLow(@RequestParam(defaultValue = "0") int page, Model model){

        try {
            Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "costPrice"));
            Page<Book> bookPage = bookRepository.findAllPage(pageable);

            model.addAttribute("bookList", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());

            loadAction(model);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/shop";
        }

        return "shop";
    }





}
