package org.example.dao;

import org.example.entity.User;

import java.util.List;

public interface UserDao {
    List<User> findAll();
    User findById(String id);
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(String id);
}
