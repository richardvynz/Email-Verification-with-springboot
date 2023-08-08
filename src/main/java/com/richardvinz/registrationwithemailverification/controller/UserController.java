package com.richardvinz.registrationwithemailverification.controller;

import com.richardvinz.registrationwithemailverification.entity.User;
import com.richardvinz.registrationwithemailverification.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User>getUser(){
        return userService.getUser();
    }
}
