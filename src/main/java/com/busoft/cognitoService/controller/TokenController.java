package com.busoft.cognitoService.controller;

import com.busoft.cognitoService.service.CognitoService;
import com.busoft.cognitoService.vo.CognitoAuthResponseVO;
import com.busoft.cognitoService.vo.UserManagementVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    @Autowired
    private CognitoService cognitoService;

    @PostMapping("/getAccessToken")
    public CognitoAuthResponseVO getAccessToken(@RequestBody UserManagementVO userManagementVO, HttpServletRequest request){
        CognitoAuthResponseVO cognitoAuthResponseVO = null;
        try{
            String accessToken = request.getHeader("Authorization");
            cognitoAuthResponseVO = cognitoService.getAccessToken(userManagementVO,accessToken);
        } catch (RuntimeException e) {
            cognitoAuthResponseVO = new CognitoAuthResponseVO();
            cognitoAuthResponseVO.setMessage(e.getMessage());
        }
        return cognitoAuthResponseVO;
    }
    @PostMapping("/validateToken")
    public CognitoAuthResponseVO validateToken(HttpServletRequest request){
        CognitoAuthResponseVO cognitoAuthResponseVO = null;
        try{
            String accessToken = request.getHeader("Authorization");
            cognitoAuthResponseVO = cognitoService.validateToken(accessToken);
        } catch (Exception e) {
            cognitoAuthResponseVO = new CognitoAuthResponseVO();
            cognitoAuthResponseVO.setMessage(e.getMessage());
        }
        return cognitoAuthResponseVO;
    }
}
