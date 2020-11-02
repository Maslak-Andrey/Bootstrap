package com.masluck.securityboot.controllers;

import com.masluck.securityboot.entities.User;
import com.masluck.securityboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class UserController {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @GetMapping("/login")
//    public String loginPage() {
//        return "login";
//    }

    @GetMapping("/user")
    public String userLoginPage() {
        return "user";
    }

    @GetMapping("/show")
    public String userPage(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", currentUser);
        return "show";
    }
}
