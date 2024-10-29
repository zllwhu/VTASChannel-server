package org.example.controller;

import org.example.dao.UserDao;
import org.example.dao.impl.UserDaoImpl;
import org.example.entity.HelloWorld;
import org.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/helloworld")
public class HelloWorldController {
    @Autowired
    private UserDaoImpl userDao;

    private HelloWorld helloWorld;

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    public HelloWorldController(HelloWorld helloWorld) {
        this.helloWorld = helloWorld;
        logger.info("hello");
    }

    @GetMapping
    public String hello() {
        logger.debug("hello-run");
        return helloWorld.hello();
    }

    @GetMapping("/users")
    public void users() {
        List<User> users = userDao.findAll();
        for (User user : users) {
            System.out.println(user.getId() + user.getPasswd());
        }
    }
}
