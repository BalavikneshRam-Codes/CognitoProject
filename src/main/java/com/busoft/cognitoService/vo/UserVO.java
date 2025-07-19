package com.busoft.cognitoService.vo;

import java.util.Collections;
import java.util.List;

public class UserVO {
    private String userId;
    private String userName;
    private String userEmail;
    private String userStatus;
    private String userMFAStatus;
    private String userCreatedDate;
    private String userUpdatedDate;
    private List<String> userMFAOptions;

    public List<String> getUserMFAOptions() {
        if(userMFAOptions == null)
            return Collections.EMPTY_LIST;
        return userMFAOptions;
    }

    public void setUserMFAOptions(List<String> userMFAOptions) {
        this.userMFAOptions = userMFAOptions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserMFAStatus() {
        return userMFAStatus;
    }

    public void setUserMFAStatus(String userMFAStatus) {
        this.userMFAStatus = userMFAStatus;
    }

    public String getUserCreatedDate() {
        return userCreatedDate;
    }

    public void setUserCreatedDate(String userCreatedDate) {
        this.userCreatedDate = userCreatedDate;
    }

    public String getUserUpdatedDate() {
        return userUpdatedDate;
    }

    public void setUserUpdatedDate(String userUpdatedDate) {
        this.userUpdatedDate = userUpdatedDate;
    }
}
