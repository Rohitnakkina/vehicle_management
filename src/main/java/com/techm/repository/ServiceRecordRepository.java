package com.techm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.entity.ServiceRecord;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
}