package com.shuyuan.judd.oauth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.shuyuan.judd.base.model.Response;
import spring.shuyuan.judd.base.utils.RSAKeyUtil;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

/**
 * 获取RSA公钥接口
 * Created by macro on 2020/6/19.
 */
@RestController
@RequestMapping("/rsa")
@Api(tags = "2. 密钥")
public class KeyPairController {

    private static final Logger logger = LoggerFactory.getLogger(KeyPairController.class);

    @Autowired
    private KeyPair keyPair;

    @GetMapping("/publicKey")
    @ApiOperation(value = "获取公钥")
    public Response<String> getPublicKey() {
        return Response.createSuccess(RSAKeyUtil.getPubKeyStr(keyPair));
    }

    @GetMapping("/test")
    @ApiOperation(value = "测试证书字符串是否正确")
    public Response<String> test(){
        try {
            PublicKey pk = RSAKeyUtil.strToPublicKey(RSAKeyUtil.getPubKeyStr(keyPair));
            if(pk.equals(keyPair.getPublic())){
                return Response.createSuccess("public key is converted correctly");
            }else{
                return Response.createNativeFail("public key is converted badly");
            }
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("",e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("",e);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            logger.error("",e);
        }
        return Response.createNativeFail("some thing wrong");
    }

}
