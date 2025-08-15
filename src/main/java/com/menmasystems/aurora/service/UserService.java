/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.component.RedisCuckooFilter;
import com.menmasystems.aurora.component.SnowflakeGenerator;
import com.menmasystems.aurora.dto.RegisterUserRequest;
import com.menmasystems.aurora.database.model.UserDocument;
import com.menmasystems.aurora.database.repository.UserRepository;
import com.menmasystems.aurora.util.SnowflakeId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final SnowflakeGenerator snowflakeGenerator;
    private final UserRepository userRepository;

    private final RedisCuckooFilter cuckooFilter;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(SnowflakeGenerator snowflakeGenerator, UserRepository userRepository, RedisCuckooFilter cuckooFilter) {
        this.snowflakeGenerator = snowflakeGenerator;
        this.userRepository = userRepository;
        this.cuckooFilter = cuckooFilter;
    }

    public Mono<SnowflakeId> createUser(RegisterUserRequest request) {
        UserDocument user = new UserDocument();
        user.setId(snowflakeGenerator.generate());
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
                .map(UserDocument::getId);
    }

    public Mono<UserDocument> getUserById(SnowflakeId id) {
        return userRepository.findById(id.id());
    }

    public Mono<UserDocument> getUserByEmail(String email) {
        if(!cuckooFilter.maybeEmailExists(email))
            return Mono.empty();

        return userRepository.findByEmail(email);
    }

    public Mono<UserDocument> getUserByLoginCredentials(String email, String password) {
        return getUserByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }
}
