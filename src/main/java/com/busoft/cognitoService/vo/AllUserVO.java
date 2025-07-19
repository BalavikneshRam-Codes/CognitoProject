package com.busoft.cognitoService.vo;

import org.apache.catalina.User;

import java.util.List;

public class AllUserVO {
    private List<UserVO> userVOS;
    private String paginationToken;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserVO> getUserVOS() {
        return userVOS;
    }

    public void setUserVOS(List<UserVO> userVOS) {
        this.userVOS = userVOS;
    }

    public String getPaginationToken() {
        return paginationToken;
    }

    public void setPaginationToken(String paginationToken) {
        this.paginationToken = paginationToken;
    }
}
