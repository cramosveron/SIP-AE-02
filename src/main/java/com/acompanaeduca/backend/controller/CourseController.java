package com.acompanaeduca.backend.controller;

import com.acompanaeduca.backend.dto.CourseDTO;
import com.acompanaeduca.backend.entity.Course;
import com.acompanaeduca.backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public List<Course> listar() {
        return courseService.obtenerTodos();
    }

    @PostMapping
    public Course guardar(@RequestBody CourseDTO courseDTO) {
        return courseService.guardar(courseDTO);
    }
}