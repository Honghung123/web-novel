package com.group17.comic.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group17.comic.dtos.request.FirebaseSignInRequest;
import com.group17.comic.dtos.request.RefreshTokenRequest;
import com.group17.comic.dtos.request.RegisterRequest;
import com.group17.comic.dtos.response.FirebaseSignInResponse;
import com.group17.comic.dtos.response.FirebaseSignUpResponse;
import com.group17.comic.dtos.response.RefreshTokenResponse;
import com.group17.comic.dtos.response.SuccessfulResponse;
import com.group17.comic.service.IFirebaseAuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {
    private final IFirebaseAuthService firebaseAuthService;

    @PostMapping("/register")
    public SuccessfulResponse<FirebaseSignUpResponse> handleRegisterUser(@RequestBody RegisterRequest request) {
        var response = firebaseAuthService.createNewUser(request);
        return new SuccessfulResponse<>(HttpStatus.OK, "Register user successfully", response);
    }

    @PostMapping("/login")
    public SuccessfulResponse<FirebaseSignInResponse> handleLoginRequest(@RequestBody FirebaseSignInRequest request) {
        FirebaseSignInResponse response = firebaseAuthService.handleLogin(request);
        return new SuccessfulResponse<>(HttpStatus.OK, "Login successfully", response);
    }

    @PostMapping("/refresh-token")
    public SuccessfulResponse<RefreshTokenResponse> handleRefreshTokenRequest(
            @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = firebaseAuthService.exchangeRefreshToken(request.refreshToken());
        return new SuccessfulResponse<>(HttpStatus.OK, "Refresh token successfully", response);
    }
}
