package com.example.demo.controller;

import com.example.service.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    private final HelloService helloService = new HelloService();
    @GetMapping("")
    public String hello() {
        return helloService.hello();
    }
}
