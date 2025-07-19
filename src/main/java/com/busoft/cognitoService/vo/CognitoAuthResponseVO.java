package com.busoft.cognitoService.vo;

public class CognitoAuthResponseVO {
    private boolean success;
    private String message;
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private String challengeName;
    private String session;
    public CognitoAuthResponseVO(){

    }
    public CognitoAuthResponseVO(boolean success, String message, String accessToken, String idToken, String refreshToken) {
        this.success = success;
        this.message = message;
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
    }

    public CognitoAuthResponseVO(boolean success, String message, String accessToken, String idToken, String refreshToken,
                      String challengeName, String session) {
        this(success, message, accessToken, idToken, refreshToken);
        this.challengeName = challengeName;
        this.session = session;
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
