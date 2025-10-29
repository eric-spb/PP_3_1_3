package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.*;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserDao userDao;

    @Autowired
    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping
    public String userPage(Model model) {
        System.out.println("=== USER PAGE - DEBUG ===");

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<User> userOptional = userDao.findByEmailWithRoles(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Отладочная информация
                System.out.println("User object: " + user);
                System.out.println("ID: " + user.getId());
                System.out.println("Name: " + user.getName());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Age: " + user.getAge());
                System.out.println("Roles: " + user.getRoles());
                System.out.println("Roles size: " + (user.getRoles() != null ? user.getRoles().size() : "null"));

                if (user.getRoles() != null) {
                    for (Role role : user.getRoles()) {
                        System.out.println("Role: " + role.getName() + " (ID: " + role.getId() + ")");
                    }
                }

                model.addAttribute("user", user);
                model.addAttribute("message", "Data loaded successfully!");

            } else {
                model.addAttribute("message", "User not found!");
            }

            return "user/user";

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("message", "Error: " + e.getMessage());
            return "user/user";
        }
    }
}