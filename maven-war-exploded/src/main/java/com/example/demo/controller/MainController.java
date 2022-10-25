package com.example.demo.controller;

import com.example.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @Autowired
    private HelloService helloService;
    @GetMapping("")
    public String hello() {
        return helloService.hello();
    }
}
