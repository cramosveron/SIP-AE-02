package com.acompanaeduca.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityDTO {

    private String titulo;

    private String descripcion;

    private Long courseId;
}