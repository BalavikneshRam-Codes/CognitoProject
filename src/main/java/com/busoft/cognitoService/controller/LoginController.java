package com.busoft.cognitoService.controller;

import com.busoft.cognitoService.service.CognitoService;
import com.busoft.cognitoService.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    private CognitoService cognitoService;
    @PostMapping("/login")
    public LoginResponseVO login(@RequestBody LoginRequestVO loginRequestVO){
        LoginResponseVO loginResponseVO = null;
        try{
            loginResponseVO = cognitoService.login(loginRequestVO);
        } catch (Exception e) {
            ResponseVO responseVO = new ResponseVO();
            responseVO.setErrorMessage(e.getMessage());
            responseVO.setErrorCode("400");
            loginResponseVO.setResponseVO(responseVO);
        }
        return loginResponseVO;
    }
    @PostMapping("/challenge")
    public CognitoAuthResponseVO challenge(@RequestBody CognitoAuthRequestVO cognitoAuthRequestVO) throws Exception {
     return cognitoService.challenge(cognitoAuthRequestVO);
    }
    @PostMapping("/forgetPassword")
    public ResponseVO forgetPassword(@RequestBody UserManagementVO userManagementVO){
        ResponseVO loginResponseVO = null;
        try{
            loginResponseVO = cognitoService.forgetPassword(userManagementVO);
        }catch (Exception e){
            ResponseVO responseVO = new ResponseVO();
            responseVO.setErrorMessage(e.getMessage());
            responseVO.setErrorCode("400");
        }
        return loginResponseVO;
    }
    @PostMapping("/confirmCodeForForgetPassword")
    public ResponseVO confirmCodeForForgetPassword(@RequestBody UserManagementVO userManagementVO){
        ResponseVO loginResponseVO = null;
        try{
            loginResponseVO = cognitoService.confirmCodeForForgetPassword(userManagementVO);
        }catch (Exception e){
            ResponseVO responseVO = new ResponseVO();
            responseVO.setErrorMessage(e.getMessage());
            responseVO.setErrorCode("400");
        }
        return loginResponseVO;
    }
}
