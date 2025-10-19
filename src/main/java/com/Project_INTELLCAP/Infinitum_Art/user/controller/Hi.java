package com.Project_INTELLCAP.Infinitum_Art.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hi {
    @GetMapping("/api/Hi")
    public String hi() {
        return "Hi there!";
    }
}
