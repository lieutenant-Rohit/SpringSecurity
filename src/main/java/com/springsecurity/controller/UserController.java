package com.springsecurity.controller;

import com.springsecurity.model.User;
import com.springsecurity.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/user")
    public User register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("STUDENT");
        }
        return userRepo.save(user);
    }

    @GetMapping("/teacher/users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/my-courses")
    public List<String> getMyCourses(Authentication authentication) {
        User user = userRepo.findByName(authentication.getName());
        return user.getCourses();
    }

}
