package com.springsecurity.controller;

import com.springsecurity.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentController {

    @Autowired
    private com.springsecurity.repo.StudentRepo studentRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/student")
    public Student register(@RequestBody Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        if (student.getRole() == null || student.getRole().isBlank()) {
            student.setRole("STUDENT");
        }
        return studentRepo.save(student);
    }

    @GetMapping("/admin/students")
    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }

    @GetMapping("/my-courses")
    public List<String> getMyCourses(Authentication authentication) {
        Student student = studentRepo.findByName(authentication.getName());
        return student.getCourses();
    }

}