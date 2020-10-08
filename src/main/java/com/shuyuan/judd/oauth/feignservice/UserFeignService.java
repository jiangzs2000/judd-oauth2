package com.shuyuan.judd.oauth.feignservice;


import com.shuyuan.judd.client.model.merchant.User;
import com.shuyuan.judd.oauth.constants.ServiceConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import spring.shuyuan.judd.base.model.Response;

@FeignClient(value = ServiceConstants.FUNDATION_SERVICE_NAME, path = "/user", decode404 = true)
public interface UserFeignService {

    @PostMapping(value = "/updatePassword")
    Response updatePassword(@RequestBody User user);

    @PostMapping(value = "/getByUserName")
    Response<User> getUserByUserName(@RequestParam("userName") String userName);

    /**
     * 获取用户加密后的密码，注意只有这个接口可以获得密码
     */
    @PostMapping("/getEncryptedPasswordById")
    Response<String> getEncryptedPasswordById(@RequestParam(name="userId") Long userId);
}
