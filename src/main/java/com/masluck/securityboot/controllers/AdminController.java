package com.masluck.securityboot.controllers;

import com.masluck.securityboot.entities.Role;
import com.masluck.securityboot.entities.User;
import com.masluck.securityboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class AdminController {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/index")
    public String index(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        model.addAttribute("userA", currentUser);

        model.addAttribute("users", userRepository.findAll());
        return "index";
    }

    @PostMapping("/admin/edit")
    public String updateUser(@RequestParam(value = "id", required = false) Long id,
                             @RequestParam(value = "username", required = false) String username,
                             @RequestParam(value = "password", required = false) String password,
                             @RequestParam(name = "roleAdmin", required = false) String roleAdmin,
                             @RequestParam(name = "roleUser", required = false) String roleUser) {
        User user = userRepository.getOne(id);

        try {
            if (!username.isEmpty()) {
                user.setUsername(username);
            }

            if (!password.isEmpty()) {
                user.setPassword(password);
            }

            Set<Role> userRoles = new HashSet<>();
            if (roleAdmin != null) {
                userRoles.add(Role.ROLE_ADMIN);
            }
            if (roleUser != null) {
                userRoles.add(Role.ROLE_USER);

            }
            user.setRoles(userRoles);
            userRepository.saveAndFlush(user);

            return "redirect:/index";
        } catch (Throwable ex) {
            return "error";
        }
    }

    @GetMapping("/admin/new")
    public String addUser(Principal principal, Model model) {
        User currentUser = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", currentUser);
        return "new";
    }

    @PostMapping("/admin/new")
    public String saveUser(User user, @RequestParam(value = "roleAdmin", required = false) String roleAdmin,
                           @RequestParam(name = "roleUser", required = false) String roleUser){
        Set<Role> userRoles = new HashSet<>();
        if (roleAdmin != null) {
            userRoles.add(Role.ROLE_ADMIN);
        }
        if (roleUser != null) {
            userRoles.add(Role.ROLE_USER);
        }
        user.setRoles(userRoles);

        userRepository.save(user);

        return "redirect:/index";
    }

    @PostMapping("/admin/{id}")
    public String delete(@PathVariable("id") long id) {
        userRepository.deleteById(id);
        return "redirect:/index";
    }
}