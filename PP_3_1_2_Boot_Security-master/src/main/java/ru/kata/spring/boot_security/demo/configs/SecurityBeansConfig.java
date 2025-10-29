package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.*;
import ru.kata.spring.boot_security.demo.service.*;

@Configuration
public class SecurityBeansConfig {
    @Bean
    public PasswordEncoder passwordEncoder(PasswordService passwordService) {
        return passwordService.getPasswordEncoder();
    }
}
