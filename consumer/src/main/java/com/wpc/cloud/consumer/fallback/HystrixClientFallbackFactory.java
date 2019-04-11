package com.wpc.cloud.consumer.fallback;

import com.wpc.cloud.consumer.bean.UserInfo;
import com.wpc.cloud.consumer.service.UserInfoClient;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HystrixClientFallbackFactory implements FallbackFactory<UserInfoClient> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HystrixClientFallbackFactory.class);

  @Override
  public UserInfoClient create(Throwable throwable) {
    //异常处理
    LOGGER.info("fallback reason was: {} " ,throwable.getMessage());
    return id -> {
      UserInfo userInfo = new UserInfo();
      userInfo.setId(-1);
      return userInfo;
    };
  }
}
