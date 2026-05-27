package com.acompanaeduca.backend.service;

import com.acompanaeduca.backend.dto.CourseDTO;
import com.acompanaeduca.backend.entity.Course;

import java.util.List;

public interface CourseService {

    List<Course> obtenerTodos();

    Course getCourseById(Long id);

    Course guardar(CourseDTO courseDTO);
}