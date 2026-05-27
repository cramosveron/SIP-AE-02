package com.acompanaeduca.backend.service;

import com.acompanaeduca.backend.dto.ActivityDTO;
import com.acompanaeduca.backend.entity.Activity;

import java.util.List;

public interface ActivityService {

    List<Activity> obtenerTodos();

    Activity guardar(ActivityDTO activityDTO);
}