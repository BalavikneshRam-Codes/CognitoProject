package com.busoft.cognitoService.vo;

import java.util.List;
import java.util.Map;

public class UserManagementVO {
    private String userName;
    private String userEmail;
    private String code;
    private String newPassword;
    private List<AttributesVO> attributes;
    private boolean mfaToggle;
    private String mfaType;
    private boolean sendEmail;

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getMfaType() {
        return mfaType;
    }

    public void setMfaType(String mfaType) {
        this.mfaType = mfaType;
    }

    public boolean isMfaToggle() {
        return mfaToggle;
    }

    public void setMfaToggle(boolean mfaToggle) {
        this.mfaToggle = mfaToggle;
    }

    public List<AttributesVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributesVO> attributes) {
        this.attributes = attributes;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
