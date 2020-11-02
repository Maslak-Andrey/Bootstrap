package com.masluck.securityboot.controllers;

import com.masluck.securityboot.entities.Role;
import com.masluck.securityboot.entities.User;
import com.masluck.securityboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AdminController {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "index";
    }

    @GetMapping("/admin/edit")
    public String editUser(@RequestParam long id, ModelMap model) {
        User user = userRepository.findById(id).get();
        model.addAttribute("user", user);
        Role[] roles = user.getRoles().toArray(new Role[0]);
        if(roles.length == 1) {
            model.addAttribute("id1", roles[0].getAuthority());
        } else if(roles.length == 2) {
            if (roles[0].getAuthority().equals("ROLE_USER")) {
                model.addAttribute("id1", roles[0].getAuthority());
                model.addAttribute("id2", roles[1].getAuthority());
            } else if (roles[0].getAuthority().equals("ROLE_ADMIN")){
                model.addAttribute("id1", roles[1].getAuthority());
                model.addAttribute("id2", roles[0].getAuthority());
            }
        }
        return "edit";
    }

    @PostMapping("/admin/edit")
    public String updateUser(@ModelAttribute User user,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             @RequestParam(name = "roleAdmin", required = false) String roleAdmin,
                             @RequestParam(name = "roleUser", required = false) String roleUser,
                             Model model) {
        try {
            if (!newPassword.isEmpty()) {
                user.setPassword(newPassword);
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
    public String addUser() {
        return "new";
    }

    @PostMapping("/admin/new")
    public String saveUser(User user, @RequestParam(value = "roleAdmin", required = false) String roleAdmin,
                           @RequestParam(name = "roleUser", required = false) String roleUser,
                           Model model){
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