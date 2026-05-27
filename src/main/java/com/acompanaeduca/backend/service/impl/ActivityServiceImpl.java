package com.acompanaeduca.backend.service.impl;

import com.acompanaeduca.backend.dto.ActivityDTO;
import com.acompanaeduca.backend.entity.Activity;
import com.acompanaeduca.backend.repository.ActivityRepository;
import com.acompanaeduca.backend.service.ActivityService;
import com.acompanaeduca.backend.service.CourseService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    private final CourseService courseService;

    @Override
    public List<Activity> obtenerTodos() {
        return activityRepository.findAll();
    }

    @Override
    public Activity guardar(ActivityDTO activityDTO) {
        Activity activity = new Activity();
        activity.setTitulo(activityDTO.getTitulo());
        activity.setDescripcion(activityDTO.getDescripcion());
        activity.setFechaEntrega(LocalDate.now());
        activity.setCourse(courseService.getCourseById(activityDTO.getCourseId()));
        return activityRepository.save(activity);
    }
}