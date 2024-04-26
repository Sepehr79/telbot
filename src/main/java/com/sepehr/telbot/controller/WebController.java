package com.sepehr.telbot.controller;

import com.sepehr.telbot.camel.CamelService;
import com.sepehr.telbot.model.entity.AdminMessageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final CamelService camelService;

    private final UserDetailsManager userDetailsManager;

    private final PasswordEncoder passwordEncoder;

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

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }

    @PostMapping("/change-user")
    public String changeUser(@RequestParam("newUser") final String newUser,
                             @RequestParam("newPass") final String newPass) {
        final UserDetails oldUser = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = User.builder()
                .password(passwordEncoder.encode(newPass))
                .username(newUser)
                .authorities(oldUser.getAuthorities())
                .build();
        UsernamePasswordAuthenticationToken userPasswordAuth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(userPasswordAuth);
        userDetailsManager.deleteUser(oldUser.getUsername());
        userDetailsManager.createUser(userDetails);
        return "redirect:/settings";
    }

}
