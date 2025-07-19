package com.busoft.cognitoService.controller;

import com.busoft.cognitoService.service.CognitoService;
import com.busoft.cognitoService.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class UserManagementController {
    @Autowired
    private CognitoService cognitoService;

    @PostMapping("/createUser")
    public ResponseVO createUser(@RequestBody UserManagementVO userManagementVO) {
        ResponseVO responseVO = null;
        try {
            responseVO = cognitoService.createUser(userManagementVO);
        } catch (Exception e) {
            responseVO = new ResponseVO();
            responseVO.setErrorMessage(e.getMessage());
            responseVO.setErrorCode("400");
        }
        return responseVO;
    }

    @GetMapping("/getUser")
    public ResponseVO GetUser(@RequestBody UserManagementVO userManagementVO) {
        ResponseVO responseVO = null;
        try {
            responseVO = cognitoService.GetUser(userManagementVO);
        } catch (Exception e) {
            responseVO = new ResponseVO();
            responseVO.setErrorMessage(e.getMessage());
            responseVO.setErrorCode("400");
        }
        return responseVO;
    }

    @PostMapping("/deleteUser")
    public ResponseVO deleteUser(@RequestBody UserManagementVO userManagementVO) {
        ResponseVO responseVO = null;
        try {
            responseVO = cognitoService.deleteUser(userManagementVO);
        } catch (Exception e) {
            responseVO = new ResponseVO();
            responseVO.setErrorMessage(e.getMessage());
            responseVO.setErrorCode("400");
        }
        return responseVO;
    }

    @GetMapping("/getAllUser")
    public AllUserVO getAllUser(@RequestBody FilterVO filterVO) {
        AllUserVO userVOS = null;
        try {
            userVOS = cognitoService.getAllUser(filterVO);
            userVOS.setStatus("SUCCESS");
        } catch (RuntimeException e) {
            userVOS = new AllUserVO();
            userVOS.setUserVOS(Collections.EMPTY_LIST);
            userVOS.setStatus(e.getMessage());
        }
        return userVOS;
    }
    @PutMapping("/updateUser")
    public ResponseVO updateUser(@RequestBody UserManagementVO userManagementVO){
        ResponseVO responseVO = null;
        try {
            responseVO = cognitoService.updateUser(userManagementVO);
        } catch (Exception e) {
            responseVO = new ResponseVO();
            responseVO.setErrorMessage(e.getMessage());
            responseVO.setErrorCode("400");
        }
        return responseVO;
    }
}
