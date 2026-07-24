package com.springsecurity.service;

import com.springsecurity.model.Student;
import com.springsecurity.model.StudentPrincipal;
import com.springsecurity.repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StudentUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepo studentRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentRepo.findByName(username);
        if (student == null) {
            throw new UsernameNotFoundException("Student not found");
        }
        return new StudentPrincipal(student);
    }

}