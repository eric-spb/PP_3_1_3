package ru.kata.spring.boot_security.demo.dao;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import ru.kata.spring.boot_security.demo.model.*;

import java.util.*;

@Repository
public interface UserRepository  {
    List<User> findAll();

    List<User> findAllWithRoles();


    Optional<User> findById(Long id);

    Optional<User> findByIdWithRoles(Long id);


    void save(User user);

    void update(User user);

    void delete(Long id);


    Optional<User> findByEmail(String email);

    Optional<User> findByEmailWithRoles(String email);

    boolean existsByEmail(String email);
}
