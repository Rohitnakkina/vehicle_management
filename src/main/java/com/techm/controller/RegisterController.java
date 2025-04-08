package com.techm.controller;

import com.techm.entity.Users;
import com.techm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/admin-register")
    public String showAdminRegistrationForm(Model model) {
        if (userRepository.findByRole(Users.Role.Admin).isPresent()) {
            return "redirect:/admin-login?error=AdminExists";
        }
        return "admin-register";
    }

    // Handle Admin Registration
    @PostMapping("/admin-register")
    public String registerAdmin(@RequestParam String userName, 
                                @RequestParam String email, 
                                @RequestParam String phone, 
                                @RequestParam String password, 
                                Model model) {

        // Check if an admin already exists
        if (userRepository.findByRole(Users.Role.Admin).isPresent()) {
            return "redirect:/admin-login?error=AdminExists";
        }

        // Create new admin user
        Users admin = new Users();
        admin.setUserName(userName);
        admin.setEmail(email);
        admin.setPhone(phone);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(Users.Role.Admin);

        // Save admin
        userRepository.save(admin);

        // Redirect to login page after successful registration
        return "redirect:/admin-login?registered=true";
    }
}
