package com.wpc.cloud.provider.controller;

import com.wpc.cloud.provider.bean.UserInfo;
import com.wpc.cloud.provider.health.HealthChecker;
import com.wpc.cloud.provider.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    private UserService userService;

    @Autowired
    private HealthChecker healthChecker;

    @ApiOperation(value = "新增用户")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public UserInfo save(@Validated @RequestBody UserInfo userInfo) {
        log.info("新增用户, request param:{}", userInfo);
        userService.save(userInfo);
        return userInfo;
    }

    @ApiOperation(value = "查询用户")
    @RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
    public UserInfo query(@PathVariable("id") Integer id) {
        log.info("查询用户, request param:{}", id);
        return userService.query(id);
    }

    @GetMapping("/health/{up}")
    public String health(@PathVariable("up") Boolean up){
        healthChecker.setUp(up);
        return up.toString();
    }
}
