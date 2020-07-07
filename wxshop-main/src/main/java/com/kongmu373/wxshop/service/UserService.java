package com.kongmu373.wxshop.service;

import com.kongmu373.wxshop.dao.UserDao;
import com.kongmu373.wxshop.generate.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private final UserDao userDao;

    private final RedisTemplate<Object, Object> template;

    private static final String PREFFIX = "tel:";

    private static final Random RANDOM = new Random();

    @Autowired
    public UserService(UserDao userDao, RedisTemplate<Object, Object> template) {
        this.userDao = userDao;
        this.template = template;
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
            // 清空缓存
            template.delete(PREFFIX + tel);
        } catch (DuplicateKeyException e) {
            return userDao.getUserByTel(tel);
        }
        return user;
    }

    public User getUserByTel(String tel) {
        ValueOperations<Object, Object> op = template.opsForValue();
        User user = (User) op.get(tel);
        if (user == null) {
            user = userDao.getUserByTel(tel);
            op.set(PREFFIX + tel, user);
            template.expire(PREFFIX + tel, RANDOM.nextInt(10) * 60, TimeUnit.SECONDS);
        }
        return user;
    }
}
