package com.prolink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/", "/login", "/register", "/admin", "/users", "/network", "/jobs", "/messaging"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
