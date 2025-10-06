package com.example.ejerciciofinal.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ejerciciofinal.dtos.CreateCourseDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO;
import com.example.ejerciciofinal.dtos.ResponseCourseDTO;
import com.example.ejerciciofinal.dtos.ResponseUserDTO;
import com.example.ejerciciofinal.model.Seat;
import com.example.ejerciciofinal.services.CourseService;
import com.example.ejerciciofinal.services.UserService;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody CreateUserDTO createUserDTO) {
        try {
            ResponseUserDTO response = userService.createUser(createUserDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createCourse")
    public ResponseEntity<?> createCourse(CreateCourseDTO createCourseDTO) {
        try {
            ResponseCourseDTO response = courseService.createCourse(createCourseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/course/assignStudents")
    public ResponseEntity<?> assignStudentsToCourse(Set<Seat> seats, Long courseId) {
        try {
            ResponseCourseDTO response = courseService.assignStudentsToCourse(seats, courseId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
