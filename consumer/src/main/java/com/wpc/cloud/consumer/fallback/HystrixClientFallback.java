package com.wpc.cloud.consumer.fallback;

import com.wpc.cloud.consumer.bean.UserInfo;
import com.wpc.cloud.consumer.service.UserInfoClient;
import org.springframework.stereotype.Component;

@Component
public class HystrixClientFallback implements UserInfoClient {

  @Override
  public UserInfo query(Integer id) {
    UserInfo userInfo = new UserInfo();
    userInfo.setId(0);
    return userInfo;
  }
}
