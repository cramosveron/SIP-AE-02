package com.acompanaeduca.backend.dto;

import com.acompanaeduca.backend.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String nombre;

    private String email;

    private String password;

    private Role rol;
}