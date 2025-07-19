package com.busoft.cognitoService.vo;

public class LoginResponseVO {
    private ResponseVO responseVO;
    private CognitoAuthResponseVO cognitoAuthResponseVO;

    public CognitoAuthResponseVO getCognitoAuthResponseVO() {
        return cognitoAuthResponseVO;
    }

    public void setCognitoAuthResponseVO(CognitoAuthResponseVO cognitoAuthResponseVO) {
        this.cognitoAuthResponseVO = cognitoAuthResponseVO;
    }

    public ResponseVO getResponseVO() {
        return responseVO;
    }

    public void setResponseVO(ResponseVO responseVO) {
        this.responseVO = responseVO;
    }
}
