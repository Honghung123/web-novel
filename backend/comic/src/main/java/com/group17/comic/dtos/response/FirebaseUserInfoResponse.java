package com.group17.comic.dtos.response;

import com.google.firebase.auth.UserRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FirebaseUserInfoResponse {
    private String userId;
    private String email;
    private String displayName;
    private String photoUrl;
    private String phoneNumber;

    public static FirebaseUserInfoResponse from(UserRecord userRecord) {
        return FirebaseUserInfoResponse.builder()
                .userId(userRecord.getUid())
                .email(userRecord.getEmail())
                .displayName(userRecord.getDisplayName())
                .photoUrl(userRecord.getPhotoUrl())
                .phoneNumber(userRecord.getPhoneNumber())
                .build();
    }
}
