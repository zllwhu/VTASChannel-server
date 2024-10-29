package org.example.dao.impl;

import org.example.dao.AccountDao;
import org.example.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("accountDaoImpl")
public class AccountDaoImpl implements AccountDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Account> findAll() {
        String sql = "select * from account";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Account.class));
    }

    @Override
    public Account findById(String id) {
        String sql = "select * from account where id = ?";
        Object[] params = new Object[]{id};
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Account.class), params);

    }

    @Override
    public boolean addAccount(Account account) {
        String sql = "insert into account(id) values(?)";
        Object[] params = new Object[]{account.getId()};
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean deleteAccount(String id) {
        String sql = "delete from account where id = ?";
        Object[] params = new Object[]{id};
        return jdbcTemplate.update(sql, params) > 0;
    }
}
