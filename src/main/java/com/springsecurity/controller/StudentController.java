package com.springsecurity.controller;

import com.springsecurity.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentController {

    @Autowired
    private com.springsecurity.repo.StudentRepo studentRepo;

    @GetMapping("/my-courses")
    public List<String> getMyCourses(Authentication authentication) {
        Student student = studentRepo.findByName(authentication.getName());
        return student.getCourses();
    }

}