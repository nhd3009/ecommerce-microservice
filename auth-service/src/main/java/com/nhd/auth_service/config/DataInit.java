package com.nhd.auth_service.config;

import com.nhd.auth_service.entity.Role;
import com.nhd.auth_service.entity.User;
import com.nhd.auth_service.repository.RoleRepository;
import com.nhd.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createRoleIfNotExists("ROLE_USER");
        createRoleIfNotExists("ROLE_EMPLOYEE");
        createRoleIfNotExists("ROLE_ADMIN");
        createUserIfNotExists(
                "customer",
                "customer",
                "customer@gmail.com",
                "ROLE_USER"
        );

        createUserIfNotExists(
                "employee",
                "employee",
                "employee@gmail.com",
                "ROLE_EMPLOYEE"
        );

        createUserIfNotExists(
                "admin",
                "admin",
                "admin@gmail.com",
                "ROLE_ADMIN"
        );
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(null, roleName)));
    }

    private void createUserIfNotExists(
            String username,
            String rawPassword,
            String email,
            String roleName
    ) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setIsEnabled(true);
        user.setRoles(Set.of(role));

        userRepository.save(user);
    }
}
