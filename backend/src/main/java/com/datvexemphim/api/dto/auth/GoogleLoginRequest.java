package com.datvexemphim.api.dto.auth;

public class GoogleLoginRequest {
    private String idToken;
    private String email;
    private String displayName;
    private String photoURL;

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPhotoURL() { return photoURL; }
    public void setPhotoURL(String photoURL) { this.photoURL = photoURL; }
}
