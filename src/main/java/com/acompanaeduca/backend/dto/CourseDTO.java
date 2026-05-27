package com.acompanaeduca.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseDTO {

    private String nombre;

    private String descripcion;

    private Long docenteId;
}