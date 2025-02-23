package com.group17.comic.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FirebaseSignUpResponse {
    private String userId;
    private String email;
    private String displayName;
}
