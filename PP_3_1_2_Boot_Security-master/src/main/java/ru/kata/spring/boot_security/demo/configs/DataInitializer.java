package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import ru.kata.spring.boot_security.demo.dao.*;
import ru.kata.spring.boot_security.demo.model.*;
import ru.kata.spring.boot_security.demo.service.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordService passwordService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public DataInitializer(UserDao userDao,
                           RoleDao roleDao,
                           PasswordService passwordService) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        clearTables();

        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
        Role userRole = createRoleIfNotExists("ROLE_USER");

        createUserIfNotExists("Admin User", "admin" ,30, "admin@admin.com", "admin", Set.of(adminRole, userRole));
        createUserIfNotExists("Regular User", "user" , 25, "user@user.com", "user", Set.of(userRole));
        createUserIfNotExists("Test User", "test" , 20, "test@test.com", "test", Set.of(userRole));
    }

    @Transactional
    public void clearTables() {
        try {
            entityManager.createNativeQuery("DELETE FROM users_roles").executeUpdate();

            entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM roles").executeUpdate();

            entityManager.createNativeQuery("ALTER TABLE users AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE roles AUTO_INCREMENT = 1").executeUpdate();
        } catch (Exception e) {
            System.out.println("Ошибка при очистке таблиц: " + e.getMessage());
        }
    }

    private Role createRoleIfNotExists(String roleName) {
        try {
            Role r = roleDao.findByName(roleName);
            if (r != null) return r;
        } catch (Exception ignored) {
        }
        Role role = new Role(roleName);
        roleDao.save(role);
        return role;
    }

    private void createUserIfNotExists(String name, String surname , int age, String email, String pwd, Set<Role> roles) {
        if (!userDao.existsByEmail(email)) {
            String hash = passwordService.encodePassword(pwd);
            User u = new User();
            u.setName(name);
            u.setSurname(surname);
            u.setAge(age);
            u.setEmail(email);
            u.setPassword(hash);
            u.setRoles(roles);
            userDao.save(u);
        }
    }
}