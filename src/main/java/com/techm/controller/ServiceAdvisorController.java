package com.techm.controller;

import com.techm.entity.ServiceRecord;
import com.techm.entity.WorkItem;
import com.techm.repository.ServiceRecordRepository;
import com.techm.repository.WorkItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/advisor")
public class ServiceAdvisorController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAdvisorController.class);

    @Autowired
    private ServiceRecordRepository serviceRecordRepository;
    @Autowired
    private WorkItemRepository workItemRepository;

    @GetMapping("/dashboard/{advisorId}")
    public String advisorDashboard(@PathVariable Long advisorId, Model model) {
        List<ServiceRecord> assignedServices = serviceRecordRepository.findAll().stream()
                .filter(sr -> sr.getServiceAdvisor() != null && 
                              sr.getServiceAdvisor().getId().equals(advisorId) && 
                              ("DUE".equals(sr.getStatus()) || "IN_PROGRESS".equals(sr.getStatus())))
                .toList();
        model.addAttribute("services", assignedServices);
        model.addAttribute("advisorId", advisorId);
        model.addAttribute("serviceRecord", new ServiceRecord());
        return "advisor";
    }

    @PostMapping("/complete/{recordId}")
    public String completeService(@PathVariable Long recordId, 
                                  @RequestParam double workCost,int workQuantity,
                                  Model model) {
        try {
            logger.info("Completing service for recordId: {}, workCost: {}", recordId, workCost);

            ServiceRecord record = serviceRecordRepository.findById(recordId)
                    .orElseThrow(() -> new IllegalArgumentException("Service Record not found with ID: " + recordId));
            if (record.getServiceAdvisor() == null) {
                throw new IllegalStateException("Service record has no assigned advisor.");
            }

            List<WorkItem> workItems = record.getWorkItems() != null ? record.getWorkItems() : new ArrayList<>();
            WorkItem item = new WorkItem();
            item.setDescription(record.getIssueType()); // Default description since it's removed from form
            item.setCost(workCost);
            item.setQuantity(workQuantity); 
            workItems.add(item);
            record.setWorkItems(workItems);
            record.setStatus("COMPLETED");
            serviceRecordRepository.save(record);
            return "redirect:/advisor/dashboard/" + record.getServiceAdvisor().getId();
        } catch (Exception e) {
            logger.error("Error completing service: {}", e.getMessage(), e);
            model.addAttribute("error", "Error completing service: " + e.getMessage());
            List<ServiceRecord> assignedServices = serviceRecordRepository.findAll().stream()
                    .filter(sr -> sr.getServiceAdvisor() != null && 
                                  sr.getServiceAdvisor().getId().equals(recordId) && 
                                  ("DUE".equals(sr.getStatus()) || "IN_PROGRESS".equals(sr.getStatus())))
                    .toList();
            model.addAttribute("services", assignedServices);
            model.addAttribute("advisorId", recordId);
            model.addAttribute("serviceRecord", new ServiceRecord());
            return "advisor";
        }
    }
}