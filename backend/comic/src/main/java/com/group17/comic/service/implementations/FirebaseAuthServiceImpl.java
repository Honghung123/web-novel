package com.group17.comic.service.implementations;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.group17.comic.dtos.request.FirebaseSignInRequest;
import com.group17.comic.dtos.request.RefreshTokenRequest;
import com.group17.comic.dtos.request.RegisterRequest;
import com.group17.comic.dtos.response.FirebaseSignInResponse;
import com.group17.comic.dtos.response.FirebaseUserInfoResponse;
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
    public FirebaseUserInfoResponse getUserInfo(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            UserRecord user = FirebaseAuth.getInstance().getUser(uid);
            return FirebaseUserInfoResponse.from(user);
        } catch (Exception e) { // Catch Firebase exceptions (FirebaseAuthException, etc.)
            throw new RuntimeException(e);
        }
    }

    @Override
    public FirebaseUserInfoResponse createNewUser(RegisterRequest registerRequest) {
        CreateRequest request = new CreateRequest();
        String formatNumber = registerRequest.phoneNumber() == null
                ? null
                : (registerRequest.phoneNumber().startsWith("0")
                        ? "+84" + registerRequest.phoneNumber().substring(1)
                        : "+84" + registerRequest.phoneNumber());
        request.setEmail(registerRequest.email());
        request.setPassword(registerRequest.password());
        request.setDisplayName(registerRequest.displayName());
        if (registerRequest.phoneNumber() != null) {
            request.setPhoneNumber(formatNumber);
        }
        request.setPhotoUrl(registerRequest.photoUrl());
        request.setEmailVerified(Boolean.TRUE);

        try {
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            return FirebaseUserInfoResponse.from(userRecord);
        } catch (FirebaseAuthException exception) {
            if (!exception.getMessage().contains(DUPLICATE_ACCOUNT_ERROR)) {
                throw new RuntimeException("Error creating user: " + exception.getMessage());
            }
            UpdateRequest updateRequest = new UpdateRequest(registerRequest.uid());
            updateRequest.setDisplayName(registerRequest.displayName());
            updateRequest.setPhoneNumber(formatNumber == null ? null : "+84" + formatNumber);
            updateRequest.setPhotoUrl(registerRequest.photoUrl());
            updateRequest.setPassword(registerRequest.password());
            try {
                UserRecord userRecord = FirebaseAuth.getInstance().updateUser(updateRequest);
                return FirebaseUserInfoResponse.from(userRecord);
            } catch (Exception e) {
                throw new RuntimeException("Error updating user: " + e.getMessage());
            }
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
