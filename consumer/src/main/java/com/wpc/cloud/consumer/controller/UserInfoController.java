package com.wpc.cloud.consumer.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.wpc.cloud.consumer.bean.UserInfo;
import com.wpc.cloud.consumer.service.UserInfoClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: 王鹏程
 * @Date: 2019/3/3 0003
 * @Time: 下午 3:10
 */
@Api(tags = "用户管理", description = "UserInfoController")
@Log4j2
@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserInfoClient userInfoClient;

    /**
     * ribbon feign hystrix 组合服务调用
     * @param id
     * @return
     */
    @ApiOperation(value = "查询用户")
    @RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
    public UserInfo query(@PathVariable("id") Integer id) {
        log.info("查询用户, request param:{}", id);
        //return restTemplate.getForObject("http://provider/user/query/"+id, UserInfo.class);
        return userInfoClient.query(id);
    }

    /**
     * ribbon restTemplate 服务调用
     * @param userInfo
     * @return
     */
    @ApiOperation(value = "新增用户")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "saveFallback")
    public String save(@Validated @RequestBody UserInfo userInfo) {
        log.info("新增用户, request param:{}", userInfo);
        ResponseEntity<UserInfo> userInfoResponseEntity = restTemplate
            .postForEntity("http://provider/user/save", userInfo, UserInfo.class);
        return "success";
    }

    /**
     * ribbon fallback
     * @param userInfo
     * @return
     */
    public String saveFallback(UserInfo userInfo) {
        return "fail";
    }

}
