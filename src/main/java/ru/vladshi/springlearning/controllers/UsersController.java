package ru.vladshi.springlearning.controllers;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vladshi.springlearning.entities.User;

@Controller
public class UsersController {

    @GetMapping("/register")
    public String showRegisterForm(@ModelAttribute("user") User user) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(@ModelAttribute("user") User user) {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        return "redirect:/";
    }
}
