package com.wpc.cloud.provider.service;

import com.wpc.cloud.provider.bean.UserInfo;
import com.wpc.cloud.provider.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 王鹏程
 * @Date: 2019/3/3 0003
 * @Time: 下午 3:14
 */
@Service
public class UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    public void save(UserInfo userInfo) {
        userInfoMapper.insertSelective(userInfo);
    }

    public UserInfo query(Integer id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }
}
