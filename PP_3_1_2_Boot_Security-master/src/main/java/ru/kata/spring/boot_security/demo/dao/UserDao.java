package ru.kata.spring.boot_security.demo.dao;


import javax.persistence.*;
import javax.transaction.*;
import org.springframework.stereotype.*;
import ru.kata.spring.boot_security.demo.model.*;

import java.util.*;

@Repository
@Transactional
@Component
public class UserDao implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<User> findAll() {
        return entityManager.createQuery("select u from User u", User.class).getResultList();
    }

    @Override
    public List<User> findAllWithRoles() {
        return entityManager.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles",
                User.class).getResultList();
    }

    public User look(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            User user = entityManager.find(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByIdWithRoles(Long id) {
        System.out.println("=== FIND BY ID WITH ROLES ===");
        System.out.println("Searching for user with ID: " + id);

        if (id == null) {
            System.out.println("❌ ID is NULL");
            return Optional.empty();
        }

        try {
            System.out.println("Attempting simple find...");
            User userSimple = entityManager.find(User.class, id);
            System.out.println("Simple find result: " + (userSimple != null ? "FOUND" : "NULL"));

            if (userSimple != null) {
                System.out.println("User email: " + userSimple.getEmail());
                userSimple.getRoles().size();
                System.out.println("Roles loaded: " + userSimple.getRoles().size());
            }

            System.out.println("Attempting JOIN FETCH query...");
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id", User.class);
            query.setParameter("id", id);

            User user = query.getSingleResult();
            System.out.println("✅ JOIN FETCH success - User: " + user.getEmail());

            return Optional.of(user);

        } catch (NoResultException e) {
            System.out.println("❌ NoResultException - User with ID " + id + " not found");
            return Optional.empty();
        } catch (Exception e) {
            System.out.println("❌ Error in findByIdWithRoles: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        try {
            entityManager.persist(user);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
        try {
            entityManager.merge(user);
        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        try {
            Optional<User> user = findById(id);

            if (user.isPresent()) {
                User userToDelete = user.get();
                entityManager.remove(userToDelete);
            } else {
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return Optional.empty();
        }
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email.trim());

            User user = query.getSingleResult();
            return Optional.of(user);

        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailWithRoles(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email", User.class);
            query.setParameter("email", email.trim());

            User user = query.getSingleResult();
            return Optional.of(user);

        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        try {
            Long count = entityManager.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email.trim())
                    .getSingleResult();

            boolean exists = count > 0;
            return exists;
        } catch (Exception e) {
            return false;
        }
    }

}
