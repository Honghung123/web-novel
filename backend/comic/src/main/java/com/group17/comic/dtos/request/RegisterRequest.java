package com.group17.comic.dtos.request;

public record RegisterRequest(
        String email, String password, String displayName, String phoneNumber, String photoUrl, String uid) {}
