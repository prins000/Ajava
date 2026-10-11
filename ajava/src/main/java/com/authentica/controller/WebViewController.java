package com.authentica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebViewController {

    @GetMapping({"/", "/index.html"})
    public String index() {
        return "index";
    }

    @GetMapping("/scanner")
    public String scanner() {
        return "scanner";
    }

    @GetMapping("/community")
    public String community() {
        return "community";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
