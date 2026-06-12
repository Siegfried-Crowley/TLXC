package org.jxiot.tlxc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/problems")
    public String problems() {
        return "problems";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/rankings")
    public String rankings() {
        return "rankings";
    }

    @GetMapping("/stats")
    public String stats() {
        return "stats";
    }

    @GetMapping("/submissions")
    public String submissions() {
        return "submissions";
    }

    @GetMapping("/contest")
    public String contest() {
        return "contest";
    }

    @GetMapping("/wrongbook")
    public String wrongbook() {
        return "wrongbook";
    }

    @GetMapping("/ai")
    public String ai() {
        return "ai";
    }

    @GetMapping("/submit")
    public String submit() {
        return "submit";
    }

    @GetMapping("/problem-detail")
    public String problemDetail() {
        return "problem-detail";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }

    @GetMapping("/admin/problems")
    public String adminProblems() {
        return "admin/problems";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }
}
