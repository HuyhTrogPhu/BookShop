package org.example.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.library.dto.AdminDto;
import org.example.library.model.Admin;
import org.example.library.service.AdminService;
import org.example.library.service.implement.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private AdminServiceImpl adminServiceImpl;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AdminService adminService;

//    @GetMapping("/")
//    public String adminLogin(Model model){
//        model.addAttribute("title", "Login Admin");
//        return "login";
//    }

    @GetMapping({"/login", "/"})
    public String loginHome(Model model){
        model.addAttribute("title", "Login Admin");
        return "login";
    }


    @RequestMapping("/index")
    public String home(Model model) {
        model.addAttribute("title", "Home Page");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return "redirect:/login";
        }
        return "index";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("title", "Register");
        model.addAttribute("adminDto", new AdminDto());
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "404";
    }

    @PostMapping("/register-new")
    public String registerNew(@Valid @ModelAttribute("adminDto") AdminDto adminDto, BindingResult bindingResult,
                              Model model) {

        try {
            if (bindingResult.hasErrors()){
                model.addAttribute("adminDto", adminDto);
                bindingResult.toString();
                return "register";
            }
            String username = adminDto.getUserName();
            Admin admin = adminServiceImpl.findByUserName(username);
            if(admin != null){
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("EmailError", "Your email has been register!");
                System.out.println("Admin not null");
                return "register";
            }
            if(adminDto.getPassword().equals(adminDto.getRepeatPassword())){
                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                adminServiceImpl.save(adminDto);
                System.out.println("Register success");
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("Success", "Register successfully!");
            }else {
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("PasswordError", "Password maybe wrong! Check again");
                System.out.println("Password same");
                return "/register";
            }

        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("Errors","The server has been wrong!");
        }


        return "register";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal){
        if(principal == null){
            return "login";
        }

        String username = principal.getName();
        Admin admin = adminService.findByUserName(username);
        model.addAttribute("admin", admin);

        return "profile";
    }

}
