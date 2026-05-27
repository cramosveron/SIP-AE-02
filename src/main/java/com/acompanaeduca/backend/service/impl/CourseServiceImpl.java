package com.acompanaeduca.backend.service.impl;

import com.acompanaeduca.backend.dto.CourseDTO;
import com.acompanaeduca.backend.entity.Course;
import com.acompanaeduca.backend.repository.CourseRepository;
import com.acompanaeduca.backend.service.CourseService;
import com.acompanaeduca.backend.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final UserService userService;

    @Override
    public List<Course> obtenerTodos() {
        return courseRepository.findAll();
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    @Override
    public Course guardar(CourseDTO courseDTO) {
        Course course = new Course();
        course.setNombre(courseDTO.getNombre());
        course.setDescripcion(courseDTO.getDescripcion());
        course.setDocente(userService.getUserById(courseDTO.getDocenteId()));
        return courseRepository.save(course);
    }
}