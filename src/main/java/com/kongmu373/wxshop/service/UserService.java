package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.dao.UserDao;
import com.kongmu373.wxshop.entity.User;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param tel
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
        } catch (PersistenceException e) {
            return userDao.getUserByTel(tel);
        }
        return user;
    }
}
