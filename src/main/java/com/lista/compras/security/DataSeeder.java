package com.lista.compras.security;

import com.lista.compras.models.User;
import com.lista.compras.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setId("1");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setName("Admin");
            userRepository.save(admin);
            System.out.println(">>> Usuário admin criado com sucesso.");
        } else {
            System.out.println(">>> Usuário admin já existe.");
        }
    }
}
