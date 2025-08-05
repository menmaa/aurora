/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.component.RedisCuckooFilter;
import com.menmasystems.aurora.dto.RegisterUserRequest;
import com.menmasystems.aurora.model.User;
import com.menmasystems.aurora.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final RedisCuckooFilter cuckooFilter;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, RedisCuckooFilter cuckooFilter) {
        this.userRepository = userRepository;
        this.cuckooFilter = cuckooFilter;
    }

    public Mono<String> createUser(RegisterUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return Mono.just(user)
                .flatMap(userRepository::save)
                .doOnSuccess(u -> {
                    cuckooFilter.addUsername(user.getUsername());
                    cuckooFilter.addEmail(user.getEmail());
                })
                .map(User::getId);
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> getUserByEmail(String email) {
        if(!cuckooFilter.maybeEmailExists(email))
            return Mono.empty();

        return userRepository.findByEmail(email);
    }

    public Mono<User> getUserByLoginCredentials(String email, String password) {
        return getUserByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }
}
