package com.app.ngila.data;

public class Authentication {
    private String AccessToken;
    private long   ExpiresIn;
    private String TokenType;
    private String RefreshToken;
    private String IdToken;

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public long getExpiresIn() {
        return ExpiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        ExpiresIn = expiresIn;
    }

    public String getTokenType() {
        return TokenType;
    }

    public void setTokenType(String tokenType) {
        TokenType = tokenType;
    }

    public String getRefreshToken() {
        return RefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        RefreshToken = refreshToken;
    }

    public String getIdToken() {
        return IdToken;
    }

    public void setIdToken(String idToken) {
        IdToken = idToken;
    }
}
