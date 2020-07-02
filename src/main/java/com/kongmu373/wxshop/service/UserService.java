package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.dao.UserDao;
import com.kongmu373.wxshop.generated.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }


    /**
     * 创建一个用户如果输入的用户不存在
     *
     * @param tel 电话号码
     * @return 返回一个用户的实体
     */
    public User createUserIfNotExist(String tel) {
        // 不能使用if，并发环境下，同时多个线程进入if，就有风险了
        User user = new User();
        user.setTel(tel);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        try {
            userDao.insertUser(user);
        } catch (DuplicateKeyException e) {
            return userDao.getUserByTel(tel);
        }
        return user;
    }

    public User getUserByTel(String tel) {
        return userDao.getUserByTel(tel);
    }
}
