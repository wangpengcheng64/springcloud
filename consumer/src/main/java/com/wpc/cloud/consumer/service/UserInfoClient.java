package com.wpc.cloud.consumer.service;

import com.wpc.cloud.consumer.bean.UserInfo;
import com.wpc.cloud.consumer.fallback.HystrixClientFallback;
import com.wpc.cloud.consumer.fallback.HystrixClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient：fallbackFactory与fallback方法不能同时使用
@FeignClient(name = "provider", /*fallback = HystrixClientFallback.class,*/ fallbackFactory = HystrixClientFallbackFactory.class) //大小写不敏感, 服务端的application name
public interface UserInfoClient {

  @GetMapping("/user/query/{id}") //这个是provider里面的controller里面的方法
  UserInfo query(@PathVariable(value = "id") Integer id); //这个名字随便写

}
