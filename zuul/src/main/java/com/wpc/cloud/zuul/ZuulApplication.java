package com.wpc.cloud.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;

//启用 zuul,自带熔断,该注解整合了@EnableCircuitBreaker、@EnableDiscoveryClient，是个组合注解，目的是简化配置。
@EnableZuulProxy
@SpringBootApplication
public class ZuulApplication {

  public static void main(String[] args) {
    SpringApplication.run(ZuulApplication.class, args);
  }

  /**
   * 如果需要路由的服务application name是consumer-v1
   * name在请求时路径为http://localhost:10005/v1/consumer/user/query/1
   * 如果application name没有version，则请求路径默认为http://localhost:10005/consumer/user/query/1
   * @return
   */
  @Bean
  public PatternServiceRouteMapper serviceRouteMapper(){
    return new PatternServiceRouteMapper("(?<name>^.+)-(?<version>v.+$)","${version}/${name}");
  }

}
