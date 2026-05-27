package com.acompanaeduca.backend.repository;

import com.acompanaeduca.backend.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}