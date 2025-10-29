package ru.kata.spring.boot_security.demo.configs;

import org.slf4j.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(SuccessUserHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse resp,
                                        Authentication auth) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        logger.debug("Пользователь вошёл с ролями: {}", roles);

        try {
            if (roles.contains("ROLE_ADMIN")) {
                logger.info("Перенаправляем ADMIN на /admin");
                resp.sendRedirect("/admin");
            } else if (roles.contains("ROLE_USER")) {
                logger.info("Перенаправляем USER на /user");
                resp.sendRedirect("/user");
            } else {
                logger.warn("У пользователя нет ролей, направляем на главную");
                resp.sendRedirect("/");
            }
        } catch (Exception e) {
            logger.error("Ошибка в SuccessUserHandler при перенаправлении", e);
            resp.sendRedirect("/");
        }
    }
}