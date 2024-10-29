package org.example.controller;

import cn.hutool.core.util.IdUtil;
import org.example.dao.impl.AccountDaoImpl;
import org.example.dao.impl.UserDaoImpl;
import org.example.entity.Account;
import org.example.entity.User;
import org.example.service.impl.AssetServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/asset")
public class UserController {
    @Autowired
    private UserDaoImpl userDao;

    @Autowired
    private AccountDaoImpl accountDao;

    @Autowired
    private AssetServiceImpl assetService;

    @PostMapping("/userLogin")
    public Map<String, Object> userLogin(User user) {
        try {
            User userData = userDao.findById(user.getId());
            Map<String, Object> result = new HashMap<>();
            if (userData.getPasswd().equals(user.getPasswd())) {
                result.put("status", "ok");
            } else {
                result.put("status", "wrong passwd failed");
            }
            return result;
        } catch (EmptyResultDataAccessException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "no specific user failed");
            return result;
        }
    }

    @PostMapping("/userRegister")
    public Map<String, Object> userRegister(User user, String amount) {
        Map<String, Object> result = new HashMap<>();
        String id = IdUtil.simpleUUID();
        user.setId(id);
        if (userDao.addUser(user)) {
            if (accountDao.addAccount(new Account(id))) {
                assetService.initialize();
                assetService.registerAssetAccount(id, BigInteger.valueOf(Long.parseLong(amount)));
                result.put("status", "ok");
                result.put("payload", id);
            }
        } else {
            result.put("status", "failed");
        }
        return result;
    }
}
