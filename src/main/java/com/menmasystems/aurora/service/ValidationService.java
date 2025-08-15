/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.service;

import com.menmasystems.aurora.component.RedisCuckooFilter;
import com.menmasystems.aurora.dto.RegisterUserRequest;
import com.menmasystems.aurora.database.repository.UserRepository;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@Service
public class ValidationService {

    private final RedisCuckooFilter cuckooFilter;
    private final UserRepository userRepository;

    public ValidationService(RedisCuckooFilter cuckooFilter, UserRepository userRepository) {
        this.cuckooFilter = cuckooFilter;
        this.userRepository = userRepository;
    }

    public Mono<Boolean> validateUsername(String username) {
        if (username == null || username.isBlank()) {
            return Mono.error(new IllegalArgumentException("Username cannot be null or empty"));
        }

        if(!cuckooFilter.maybeUsernameExists(username)) {
            return Mono.just(true);
        }

        return userRepository.existsByUsername(username).map(exists -> !exists);
    }

    public Mono<Boolean> validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return Mono.error(new IllegalArgumentException("Email cannot be null or empty"));
        }

        if(!cuckooFilter.maybeEmailExists(email)) {
            return Mono.just(true);
        }

        return userRepository.existsByEmail(email).map(exists -> !exists);
    }

    public Mono<Void> assertUsernameAndEmailAvailability(RegisterUserRequest request, MethodParameter param) {
        Mono<Boolean> validateUsername = validateUsername(request.getUsername());
        Mono<Boolean> validateEmail = validateEmail(request.getEmail());

        return Mono.zip(validateUsername, validateEmail)
                .flatMap(tuple -> {
                    if(tuple.getT1() && tuple.getT2()) {
                        return Mono.empty();
                    }

                    BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");

                    if(!tuple.getT1()) {
                        bindingResult.addError(
                                new FieldError("request", "username", request.getUsername(), false, null, null, "Username is not available")
                        );
                    }

                    if(!tuple.getT2()) {
                        bindingResult.addError(
                                new FieldError("request", "email", request.getEmail(), false, null, null, "Email is not available")
                        );
                    }

                    return Mono.error(new WebExchangeBindException(param, bindingResult));
                });
    }
}
