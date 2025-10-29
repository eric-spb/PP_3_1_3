package ru.kata.spring.boot_security.demo.dao;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import ru.kata.spring.boot_security.demo.model.*;

import java.util.*;

@Repository
public interface RoleRepository {
    List<Role> findAll();

    Role findById(long id);

    Role findByName(String name);

    void save(Role role);

    void deleteById(Long id);
}
