package com.example.digitalcharitygovernance.controllers;

import com.example.digitalcharitygovernance.security.AuthDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final AuthDetails authDetails;

    public LoginController(AuthDetails authDetails) {
        this.authDetails = authDetails;
    }


    @GetMapping("/")
    public String index() {
        return "redirect:/meetings/list";
    }

    @GetMapping("/home")
    public String home(Model model) {

        model.addAttribute("authUsername",authDetails.getAuthenticatedUser().getUsername());
        model.addAttribute("authLevel",authDetails.getAuthenticatedUser().getAuthorities().toString());

        System.out.println(authDetails.getAuthenticatedUser().getAuthorities().toString());

        return "/home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

}
