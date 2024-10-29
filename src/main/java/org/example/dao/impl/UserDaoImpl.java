package org.example.dao.impl;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDaoImpl")
public class UserDaoImpl implements UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sql = "select * from user";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public User findById(String id) {
        String sql = "select * from user where id = ?";
        Object[] params = new Object[]{id};
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), params);
    }

    @Override
    public boolean addUser(User user) {
        String sql = "insert into user(id, passwd) values(?,?)";
        Object[] params = new Object[]{user.getId(), user.getPasswd()};
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "update user set passwd = ? where id = ?";
        Object[] params = new Object[]{user.getPasswd(), user.getId()};
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean deleteUser(String id) {
        String sql = "delete from user where id = ?";
        Object[] params = new Object[]{id};
        return jdbcTemplate.update(sql, params) > 0;
    }
}
