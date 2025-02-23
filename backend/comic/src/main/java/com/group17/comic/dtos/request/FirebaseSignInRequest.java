package com.group17.comic.dtos.request;

public record FirebaseSignInRequest(String email, String password, boolean returnSecureToken) {}
