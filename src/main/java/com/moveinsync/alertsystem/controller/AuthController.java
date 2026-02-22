package com.moveinsync.alertsystem.controller;

import com.moveinsync.alertsystem.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody Map<String,String> body){

        String username = body.get("username");
        String password = body.get("password");

        // simple hardcoded login
        if("admin".equals(username) && "admin".equals(password)){
            String token = jwtUtil.generateToken(username);

            Map<String,String> res = new HashMap<>();
            res.put("token", token);
            return res;
        }

        throw new RuntimeException("Invalid login");
    }
}