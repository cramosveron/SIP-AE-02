package com.acompanaeduca.backend.controller;

import com.acompanaeduca.backend.dto.ActivityDTO;
import com.acompanaeduca.backend.entity.Activity;
import com.acompanaeduca.backend.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public List<Activity> listar() {
        return activityService.obtenerTodos();
    }

    @PostMapping
    public Activity guardar(@RequestBody ActivityDTO activityDTO) {
        return activityService.guardar(activityDTO);
    }
}