package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.dao.RoleDao;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final UserDao userDao;
    private final RoleDao roleDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public AdminController(UserService userService, UserDao userDao, RoleDao roleDao) {
        this.userService = userService;
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @GetMapping
    public String adminPage(Model model) {
        List<User> users = userDao.findAllWithRoles();
        model.addAttribute("users", users);
        return "admin/admin";
    }

    @GetMapping("/view")
    public String show(@RequestParam("id") Long id, Model model) {
        model.addAttribute("person", userDao.look(id));
        return "admin/view";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleDao.findAll());
        return "admin/new";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        System.out.println("=== EDIT USER ID: " + id + " ===");

        try {
            // Временное решение - используем простой find
            User user = entityManager.find(User.class, id);

            if (user == null) {
                System.out.println("❌ User not found with ID: " + id);
                return "redirect:/admin?error=user_not_found";
            }

            System.out.println("✅ User found: " + user.getEmail());

            // Принудительно загружаем роли
            user.getRoles().size();
            System.out.println("Roles count: " + user.getRoles().size());

            model.addAttribute("user", user);
            model.addAttribute("allRoles", roleDao.findAll());
            return "admin/edit";

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin?error=server_error";
        }
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user,
                           @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {
        if (roleIds != null) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : roleIds) {
                Role role = roleDao.findById(roleId);
                if (role != null) {
                    roles.add(role);
                }
            }
            user.setRoles(roles);
        }
        if (user.getId() == null) {
            userDao.save(user);
        } else {
            userDao.update(user);
        }
        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String showDeleteConfirmation(@PathVariable("id") Long id, Model model) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            model.addAttribute("user", user);
            return "admin/delete";
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userDao.delete(id);
        return "redirect:/admin";
    }
}