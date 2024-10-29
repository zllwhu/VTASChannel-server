package org.example.dao;

import org.example.entity.Account;

import java.util.List;

public interface AccountDao {
    List<Account> findAll();
    Account findById(String id);
    boolean addAccount(Account account);
    boolean deleteAccount(String id);
}
