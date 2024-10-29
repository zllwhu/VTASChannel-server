package org.example.entity;

import org.springframework.stereotype.Component;

@Component
public class HelloWorld {
    public String hello() {
        return "Hello World!";
    }
}
