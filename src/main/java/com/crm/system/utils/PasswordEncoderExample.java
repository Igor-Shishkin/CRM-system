package com.crm.system.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderExample {
    public static void main(String[] args) {
        String password = "000000";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("password: " + password);
        System.out.println("encoded password: " + encodedPassword);
    }
}
