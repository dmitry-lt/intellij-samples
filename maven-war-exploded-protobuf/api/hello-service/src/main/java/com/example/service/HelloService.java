package com.example.service;

import com.address.protobuf.AddressBookProtos;

import java.util.Random;

public class HelloService {
    public String hello() {
        String email = "test@example.com";
        int id = new Random().nextInt();
        String name = "Test";
        String number = "01234567890";
        AddressBookProtos.Person person =
                AddressBookProtos.Person.newBuilder()
                        .setId(id)
                        .setName(name)
                        .setEmail(email)
                        .addNumbers(number)
                        .build();
        return "hello " + person.getName();
    }
}
