package com.acompanaeduca.backend.service;

import com.acompanaeduca.backend.dto.UserDTO;
import com.acompanaeduca.backend.entity.User;

import java.util.List;

public interface UserService {

    List<User> obtenerTodos();

    User getUserById(Long id);

    User guardar(UserDTO userDTO);
}