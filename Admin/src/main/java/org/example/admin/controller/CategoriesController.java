package org.example.admin.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.library.model.Caterogy;

import org.example.library.repository.CategoryRepository;
import org.example.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HttpSession session;

    private static final int pageSize = 15;

    @GetMapping("/categories")
    public String categories(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        if(principal == null){
            return "redirect:/login";
        }
        List<Caterogy> caterogyList = categoryRepository.findAll();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Caterogy> category = categoryRepository.pageCategory(pageable);
        model.addAttribute("categories", category.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", category.getTotalPages());
        model.addAttribute("size", caterogyList.size());
        model.addAttribute("title", "Categories");
        return "list-categorie";
    }

    @GetMapping("/manager-categories")
    public String managerCategories(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        if(principal == null){
            return "redirect:/login";
        }
        List<Caterogy> caterogyList = categoryRepository.findAll();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Caterogy> category = categoryRepository.pageCategory(pageable);
        model.addAttribute("categories", category.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", category.getTotalPages());
        model.addAttribute("size", caterogyList.size());
        model.addAttribute("title", "Categories");
        model.addAttribute("categoryNew", new Caterogy());
        return "manager-categories";
    }

    //    Add new
    @PostMapping("/addCategory")
    public String addCategory(Model model, @ModelAttribute("categoryNew") Caterogy caterogy, RedirectAttributes redirectAttributes) {
        try {
            categoryRepository.save(caterogy);
            redirectAttributes.addFlashAttribute("success", "Added Successfully!");
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed add because duplicate category name!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed add!");
        }
        return "redirect:/manager-categories";
    }

    //    Get edit
    @GetMapping("/edit")
    public String showEditCategory(@RequestParam("categoryId") Long id, Model model) {

        try {
            Caterogy caterogy = categoryRepository.findById(id).get();
            model.addAttribute("getCategory", caterogy);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manager-categories";
        }
        return "editCategory";
    }

    //    Edit
    @PostMapping("/editCategory")
    public String editCategory(@ModelAttribute("getCategory") Caterogy updatedCategory, RedirectAttributes redirectAttributes) {
        try {
            Caterogy foundCategory = categoryRepository.findById(updatedCategory.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + updatedCategory.getId()));
            foundCategory.setName(updatedCategory.getName());
            categoryRepository.save(foundCategory);
            redirectAttributes.addFlashAttribute("success", "Updated Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update!");
        }
        return "redirect:/manager-categories";
    }



    //    Delete
    @GetMapping("/remove")
    public String removeCategory(Model model, @RequestParam("categoryId") Long id, RedirectAttributes redirectAttributes) {
        try {
            Caterogy category = categoryRepository.findById(id).orElse(null);
            if (category != null) {
                categoryRepository.delete(category);
                redirectAttributes.addFlashAttribute("success", "Removed Successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Category not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to remove!");
        }
        return "redirect:/manager-categories";
    }

//    Search by keyword
    @PostMapping("/searchCategory")
    public String searchCategory(@RequestParam(defaultValue = "0") int page, @RequestParam("keyword") String keyword, Model model){

        try {
            if(keyword == null){
               keyword = (String) session.getAttribute("keyword");
            } else {
                session.setAttribute("keyword", keyword);
            }

            if(keyword == null){
                return "redirect:/list-categorie";
            }

            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Caterogy> caterogyPage = categoryService.searchCategory(keyword, pageable);
            model.addAttribute("categories", caterogyPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", caterogyPage.getTotalPages());
            model.addAttribute("size", caterogyPage.getSize());
            model.addAttribute("title", "Categories");
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/list-categorie";
        }

        return "list-categorie";
    }

    @PostMapping("/searchManagerCategory")
    public String searchManagerCategory(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page, Model model){

        try {
            if(keyword == null){
                keyword = (String) session.getAttribute("keyword");
            } else {
                session.setAttribute("keyword", keyword);
            }
            if(keyword == null){
                return "redirect:/manager-categories";
            }

            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Caterogy> caterogyPage = categoryService.searchCategory(keyword, pageable);
            model.addAttribute("categories", caterogyPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", caterogyPage.getTotalPages());
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manager-categories";
        }


        return "manager-categories";
    }

}
