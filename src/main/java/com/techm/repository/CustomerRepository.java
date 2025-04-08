package com.techm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}