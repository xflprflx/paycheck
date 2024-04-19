package com.xflprflx.paycheck.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownController {

    @PostMapping("/shutdown")
    public void shutdown(){
        System.exit(0);
    }
}