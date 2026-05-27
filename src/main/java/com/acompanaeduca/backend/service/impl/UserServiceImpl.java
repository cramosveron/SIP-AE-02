package com.acompanaeduca.backend.service.impl;

import com.acompanaeduca.backend.dto.UserDTO;
import com.acompanaeduca.backend.entity.User;
import com.acompanaeduca.backend.repository.UserRepository;
import com.acompanaeduca.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> obtenerTodos() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User guardar(UserDTO userDTO) {
        User user = new User();
        user.setNombre(userDTO.getNombre());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRol(userDTO.getRol());
        return userRepository.save(user);
    }
}