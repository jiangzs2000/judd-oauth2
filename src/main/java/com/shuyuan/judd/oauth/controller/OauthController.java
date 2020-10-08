package com.shuyuan.judd.oauth.controller;

import com.alibaba.fastjson.JSONObject;
import com.shuyuan.judd.client.model.merchant.User;
import com.shuyuan.judd.client.utils.JWTUtil;
import com.shuyuan.judd.oauth.constants.ServiceConstants;
import com.shuyuan.judd.oauth.feignservice.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.shuyuan.judd.base.model.Response;
import spring.shuyuan.judd.base.utils.AESUtils;
import spring.shuyuan.judd.base.utils.EncryptUtil;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/oauth")
@Api(tags = "1. 用户登录相关操作")
public class OauthController {

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private KeyPair keyPair;

    @RequestMapping(value ="/login" ,produces = "application/json;charset=UTF-8")
    @ApiOperation(value = "用户登录")
    public Response<Map<String,String>> login(@RequestBody User curUser) throws InvalidKeySpecException, NoSuchAlgorithmException {
        User user = userFeignService.getUserByUserName(curUser.getUserName()).getData();
        log.debug("the logging in user is{}",user);
        if(user != null){
            String encryptedPassword = userFeignService.getEncryptedPasswordById(user.getId()).getData();
            //if(AESUtils.encrypt(curUser.getPassword(), null).contentEquals(encryptedPassword)){
            if(EncryptUtil.getEncryptedPassword(curUser.getPassword(),curUser.getUserName()).equals(encryptedPassword)){
                //create jwt token
                String token = JWTUtil.createToken(user, keyPair.getPrivate());
                log.debug("login successed, token is:{}",token);
                Map<String, String> map = new HashMap<>();
                map.put("token", token);
                return Response.createSuccess(map);
            }else{
                return Response.createNativeFail("用户名密码错误。");
            }
        }else{
            return Response.createNativeFail("用户名密码错误。");
        }
    }
    @PostMapping("/refreshtoken")
    @ApiOperation(value = "用现有的token换取新token")
    public Response<String> refreshToken(@RequestParam("token") String token){
        User user =  JWTUtil.getUserFromJWT(token, keyPair.getPublic());
        if(user != null){
            log.debug("the token's user is {}", user);
            return Response.createSuccess(JWTUtil.createToken(user, keyPair.getPrivate()));
        }else{
            return Response.createNativeFail("非法token.");
        }
    }

    @GetMapping("/updatePassword")
    @ApiOperation(value = "更换密码，并生成新的token")
    public Response<String> updatePassword(@RequestHeader(value = ServiceConstants.CURRENT_LOGIN_USER) String curUserStr, @RequestParam(name="oldPwd") String oldPwd, @RequestParam(name="newPwd") String newPwd) throws InvalidKeySpecException, NoSuchAlgorithmException {
        User curUser = JSONObject.parseObject(curUserStr, User.class);
        User user = userFeignService.getUserByUserName(curUser.getUserName()).getData();
        if(user == null){
            return Response.createNativeFail("用户不存在");
        }
        String encryptedPassword = userFeignService.getEncryptedPasswordById(user.getId()).getData();
        //if(AESUtils.encrypt(curUser.getPassword(), null).contentEquals(encryptedPassword)){
        if(EncryptUtil.getEncryptedPassword(oldPwd,user.getUserName()).equals(encryptedPassword)){
            user.setPassword(newPwd);
            userFeignService.updatePassword(user);
            return Response.createSuccess(JWTUtil.createToken(user, keyPair.getPrivate()));
        }else{
            return Response.createNativeFail("用户名密码错误。");
        }
    }

    @PostMapping("/logout")
    @ApiOperation(value = "退出登录")
    public Response<String> logout(){
        return Response.createSuccess("退出成功");
    }


}
