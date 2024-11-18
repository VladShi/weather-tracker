package ru.vladshi.springlearning.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class IndexPageController {

    @GetMapping("/")
    public String index(@RequestParam(value = "name", required = false) String name, Model model) {
        model.addAttribute("name", name == null ? "Stranger" : name);
        return "index";
    }

    @GetMapping("/{name}")
    public String indexName(@PathVariable("name") String name, Model model) {
        model.addAttribute("name", name == null ? "Stranger" : name);
        return "index";
    }
}