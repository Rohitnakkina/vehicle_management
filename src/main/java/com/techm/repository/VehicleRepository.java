package com.techm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}