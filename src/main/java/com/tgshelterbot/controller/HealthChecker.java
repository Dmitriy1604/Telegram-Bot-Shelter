package com.tgshelterbot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthChecker {

    @GetMapping("/info")
    public String getHealthState(){
        return "I am fine and working :) ";
    }
}
