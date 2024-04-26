package com.sepehr.telbot.controller;

import com.sepehr.telbot.camel.CamelService;
import com.sepehr.telbot.model.entity.AdminMessageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final CamelService camelService;

    @GetMapping("/")
    public String redirectMainPage() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String mainPage(Model model) {
        model.addAttribute("adminMessage", AdminMessageModel.builder().build());
        return "index";
    }

    @PostMapping("/send-message")
    public String sendMessage(@ModelAttribute AdminMessageModel adminMessageModel) {
        camelService.sendAdminMessage(adminMessageModel);
        return "redirect:/index";
    }

}
