
package com.techm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/admin-login")
    public String adminLogin() {
        return "admin-login";
    }
    


    @GetMapping("/")
    public String Login() {
        return "admin-login";
    }
    
}


