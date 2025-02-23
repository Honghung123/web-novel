package com.group17.comic.service.implementations;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.group17.comic.dtos.request.FirebaseSignInRequest;
import com.group17.comic.dtos.request.RefreshTokenRequest;
import com.group17.comic.dtos.request.RegisterRequest;
import com.group17.comic.dtos.response.FirebaseSignInResponse;
import com.group17.comic.dtos.response.FirebaseSignUpResponse;
import com.group17.comic.dtos.response.RefreshTokenResponse;
import com.group17.comic.service.IFirebaseAuthService;

@Service
@Primary
public class FirebaseAuthServiceImpl implements IFirebaseAuthService {
    private final String FIREBASE_API_KEY = "AIzaSyA4Ix8zhhpUXEbKeOZqIL93Z9OiQqxvpWM";
    private static final String API_KEY_PARAM = "key";
    private static final String INVALID_CREDENTIALS_ERROR = "INVALID_LOGIN_CREDENTIALS";
    private static final String SIGN_IN_BASE_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword";

    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
    private static final String INVALID_REFRESH_TOKEN_ERROR = "INVALID_REFRESH_TOKEN";
    private static final String REFRESH_TOKEN_BASE_URL = "https://securetoken.googleapis.com/v1/token";

    // @Value("${com.baeldung.firebase.web-api-key}")
    // private String webApiKey;
    private static final String DUPLICATE_ACCOUNT_ERROR = "EMAIL_EXISTS";

    @Override
    public FirebaseSignUpResponse createNewUser(RegisterRequest registerRequest) {
        CreateRequest request = new CreateRequest();
        request.setEmail(registerRequest.email());
        request.setPassword(registerRequest.password());
        request.setDisplayName(registerRequest.displayName());
        request.setEmailVerified(Boolean.TRUE);

        try {
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            return FirebaseSignUpResponse.builder()
                    .userId(userRecord.getUid())
                    .email(userRecord.getEmail())
                    .displayName(userRecord.getDisplayName())
                    .build();
        } catch (FirebaseAuthException exception) {
            if (exception.getMessage().contains(DUPLICATE_ACCOUNT_ERROR)) {
                throw new RuntimeException("Account with given email-id already exists");
            }
            throw new RuntimeException(exception);
        }
    }

    @Override
    public FirebaseSignInResponse handleLogin(FirebaseSignInRequest firebaseSignInRequest) {
        try {
            return RestClient.create(SIGN_IN_BASE_URL)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(API_KEY_PARAM, FIREBASE_API_KEY)
                            .build())
                    .body(firebaseSignInRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(FirebaseSignInResponse.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getResponseBodyAsString().contains(INVALID_CREDENTIALS_ERROR)) {
                throw new RuntimeException("Invalid login credentials provided");
            }
            throw new RuntimeException(exception);
        }
    }

    @Override
    public RefreshTokenResponse exchangeRefreshToken(String refreshToken) {
        RefreshTokenRequest requestBody = new RefreshTokenRequest(REFRESH_TOKEN_GRANT_TYPE, refreshToken);
        return sendRefreshTokenRequest(requestBody);
    }

    private RefreshTokenResponse sendRefreshTokenRequest(RefreshTokenRequest refreshTokenRequest) {
        try {
            return RestClient.create(REFRESH_TOKEN_BASE_URL)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(API_KEY_PARAM, FIREBASE_API_KEY)
                            .build())
                    .body(refreshTokenRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(RefreshTokenResponse.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getResponseBodyAsString().contains(INVALID_REFRESH_TOKEN_ERROR)) {
                throw new RuntimeException("Invalid refresh token provided");
            }
            throw new RuntimeException(exception);
        }
    }
}
