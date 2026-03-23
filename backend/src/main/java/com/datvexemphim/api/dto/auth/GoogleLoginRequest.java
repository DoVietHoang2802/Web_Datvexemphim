package com.datvexemphim.api.dto.auth;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String idToken;
    private String email;
    private String displayName;
    private String photoURL;
}
