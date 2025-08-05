/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.controller;

import com.menmasystems.aurora.auth.AuroraAuthenticationToken;
import com.menmasystems.aurora.dto.LoginUserRequest;
import com.menmasystems.aurora.dto.LoginUserResponse;
import com.menmasystems.aurora.dto.RegisterUserRequest;
import com.menmasystems.aurora.dto.RegisterUserResponse;
import com.menmasystems.aurora.exception.ApiException;
import com.menmasystems.aurora.exception.ErrorCode;
import com.menmasystems.aurora.service.SessionService;
import com.menmasystems.aurora.service.UserService;
import com.menmasystems.aurora.service.ValidationService;
import jakarta.validation.Valid;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;


@RestController
@RequestMapping("/auth")
class AuthController {

    private final ValidationService validationService;
    private final UserService userService;
    private final SessionService sessionService;

    public AuthController(ValidationService validationService, UserService userService, SessionService sessionService) {
        this.validationService = validationService;
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        try {
            Method method = getClass().getMethod("registerUser", request.getClass());
            MethodParameter parameter = new MethodParameter(method, 0);

            return validationService.assertUsernameAndEmailAvailability(request, parameter)
                    .then(userService.createUser(request))
                    .map(RegisterUserResponse::new);
        } catch (NoSuchMethodException e) {
            return Mono.error(e);
        }
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<LoginUserResponse> loginUser(@Valid @RequestBody LoginUserRequest request) {
        return userService.getUserByLoginCredentials(request.getEmail(), request.getPassword())
                .flatMap(user -> sessionService.createSession(user.getId()))
                .map(LoginUserResponse::new)
                .switchIfEmpty(Mono.error(new ApiException(ErrorCode.USER_AUTH_INVALID_CREDS)));
    }

    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> logoutUser(@AuthenticationPrincipal AuroraAuthenticationToken auth) {
        return sessionService.invalidateSession(auth);
    }
}
