package com.acompanaeduca.backend.repository;

import com.acompanaeduca.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}