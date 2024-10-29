package org.example.controller;

import org.example.dao.impl.AccountDaoImpl;
import org.example.entity.Account;
import org.example.entity.TableItem;
import org.example.service.impl.AssetServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/asset")
public class AssetController {
    @Autowired
    private AccountDaoImpl accountDao;

    @Autowired
    private AssetServiceImpl assetService;

    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);

    @GetMapping("query")
    public BigInteger query(String account) {
        assetService.initialize();
        return assetService.queryAssetValue(account);
    }

    @GetMapping("all")
    public List<TableItem> all() {
        assetService.initialize();
        List<Account> accountList = accountDao.findAll();
        List<TableItem> tableItemList = new ArrayList<>();
        for (Account account : accountList) {
            String id = account.getId();
            String amount = assetService.queryAssetValue(id).toString();
            tableItemList.add(new TableItem(id, amount));
        }
        return tableItemList;
    }
}
