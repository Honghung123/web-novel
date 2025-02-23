package com.group17.comic.service;

import org.springframework.stereotype.Service;

import com.group17.comic.dtos.request.FirebaseSignInRequest;
import com.group17.comic.dtos.request.RegisterRequest;
import com.group17.comic.dtos.response.FirebaseSignInResponse;
import com.group17.comic.dtos.response.FirebaseSignUpResponse;
import com.group17.comic.dtos.response.RefreshTokenResponse;

@Service
public interface IFirebaseAuthService {
    public FirebaseSignUpResponse createNewUser(RegisterRequest registerRequest);

    public FirebaseSignInResponse handleLogin(FirebaseSignInRequest firebaseSignInRequest);

    public RefreshTokenResponse exchangeRefreshToken(String refreshToken);
}
