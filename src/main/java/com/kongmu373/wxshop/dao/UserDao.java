package com.kongmu373.wxshop.dao;

import com.kongmu373.wxshop.entity.User;
import com.kongmu373.wxshop.entity.UserExample;
import com.kongmu373.wxshop.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDao {
    private final SqlSessionFactory sqlSessionFactory;

    @Autowired
    public UserDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void insertUser(User user) {
        try (final SqlSession session = sqlSessionFactory.openSession(true)) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.insert(user);
        }

    }

    public User getUserByTel(String tel) {
        try (final SqlSession session = sqlSessionFactory.openSession(true)) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            UserExample userExample = new UserExample();
            userExample.createCriteria().andTelEqualTo(tel);
            return mapper.selectByExample(userExample).get(0);
        }
    }
}
