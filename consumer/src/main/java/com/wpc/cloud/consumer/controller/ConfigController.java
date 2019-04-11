package com.wpc.cloud.consumer.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RefreshScope //Config-配置动态刷新,修改profile 配置后，只需向应用的/actuator/refresh 端点发送POST请求，即可刷新该属性。
 * 命令窗口执行 curl -X POST http://localhost:10002/actuator/refresh即可
 *
 * @Author: 王鹏程
 * @Date: 2019/3/3 0003
 * @Time: 下午 3:10
 */
@Api(tags = "配置管理", description = "ConfigController")
@Log4j2
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    @Value("${test-name}")
    private String testName;

    @ApiOperation(value = "config测试")
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String configTest() {
        log.info("config测试, request param:{}", "");
        return testName;
    }

}
