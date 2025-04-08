package com.techm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.entity.WorkItem;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {
}