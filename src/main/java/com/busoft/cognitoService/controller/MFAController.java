package com.busoft.cognitoService.controller;

import com.busoft.cognitoService.service.CognitoService;
import com.busoft.cognitoService.vo.ResponseVO;
import com.busoft.cognitoService.vo.UserManagementVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MFAController {
    @Autowired
    private CognitoService cognitoService;
    @PostMapping("/getSecretCodeForTOPT")
    public ResponseVO createMFA(@RequestBody UserManagementVO userManagementVO, HttpServletRequest request){
        ResponseVO responseVO = null;
        if(request.getHeader("Authorization") != null) {
            try {
                String accessToken = request.getHeader("Authorization");
                responseVO = cognitoService.getSecretCodeForTOPT(userManagementVO,accessToken);
            } catch (Exception e) {
                responseVO = new ResponseVO();
                responseVO.setErrorMessage(e.getMessage());
                responseVO.setErrorCode("400");
            }
        }
        return responseVO;
    }
    @PostMapping("/confirmTOPT")
    public ResponseVO confirmTOPT(@RequestBody UserManagementVO userManagementVO,HttpServletRequest request){
        ResponseVO responseVO = null;
        if(request.getHeader("Authorization") != null) {
            try {
                String accessToken = request.getHeader("Authorization");
                responseVO = cognitoService.confirmTOPT(userManagementVO,accessToken);
            } catch (Exception e) {
                responseVO = new ResponseVO();
                responseVO.setErrorMessage(e.getMessage());
                responseVO.setErrorCode("400");
            }
        }
        return responseVO;
    }
    @PostMapping("/mfaToggle")
    public ResponseVO mfaToggle(@RequestBody UserManagementVO userManagementVO){
        ResponseVO responseVO = null;
        try {
            responseVO = cognitoService.mfaToggle(userManagementVO);
        } catch (Exception e) {
            responseVO = new ResponseVO();
            responseVO.setErrorMessage(e.getMessage());
            responseVO.setErrorCode("400");
        }
        return responseVO;
    }
}
