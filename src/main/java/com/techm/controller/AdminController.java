
package com.techm.controller;

import com.techm.entity.*;
import com.techm.repository.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ServiceRecordRepository serviceRecordRepository;
    @Autowired
    private WorkItemRepository workItemRepository;
    @Autowired
    private ServiceAdvisorRepository serviceAdvisorRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    
    
 


    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<ServiceRecord> history = serviceRecordRepository.findAll();
        model.addAttribute("history", history);
        return "admin";
    }

    @GetMapping("/add-customer")
    public String showAddCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "add-customer";
    }

    @PostMapping("/add-customer")
    public String addCustomer(@ModelAttribute Customer customer) {
        customerRepository.save(customer);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/vehicles")
    public String listVehicles(Model model) {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        List<ServiceRecord> activeServices = serviceRecordRepository.findAll().stream()
                .filter(sr -> "IN_PROGRESS".equals(sr.getStatus()) || "COMPLETED".equals(sr.getStatus()))
                .toList();
        List<Long> servicedVehicleIds = activeServices.stream()
                .map(sr -> sr.getVehicle().getId())
                .collect(Collectors.toList());
        List<Vehicle> availableVehicles = allVehicles.stream()
                .filter(vehicle -> !servicedVehicleIds.contains(vehicle.getId()))
                .toList();
        model.addAttribute("vehicles", availableVehicles);
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("advisors", serviceAdvisorRepository.findAll());
        return "vehicles";
    }




@GetMapping("/vehicles/edit/{id}")
public String editVehicle(@PathVariable Long id, Model model) {
    Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Vehicle ID: " + id));

    model.addAttribute("vehicle", vehicle);
    model.addAttribute("customers", customerRepository.findAll());
    model.addAttribute("advisors", serviceAdvisorRepository.findAll());

    return "vehicles";
}


@PostMapping("/vehicles/save")
public String saveOrUpdateVehicle(@ModelAttribute Vehicle vehicle) {
    if (vehicle.getId() != null) {
        // If ID exists, update the existing record
        Vehicle existingVehicle = vehicleRepository.findById(vehicle.getId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        existingVehicle.setRegistrationNumber(vehicle.getRegistrationNumber());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setCustomer(vehicle.getCustomer());
        existingVehicle.setLastServiceDate(vehicle.getLastServiceDate());

        vehicleRepository.save(existingVehicle);
    } else {
        // If no ID, it's a new vehicle
        vehicleRepository.save(vehicle);
    }

    return "redirect:/admin/vehicles";
}


    @GetMapping("/vehicles/delete/{id}")
    public String deleteVehicle(@PathVariable Long id, Model model) {
        try {
            Vehicle vehicle = vehicleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));
            vehicleRepository.delete(vehicle);
            return "redirect:/admin/vehicles";
        } catch (Exception e) {
            model.addAttribute("error", "Cannot delete vehicle: " + e.getMessage());
            model.addAttribute("vehicles", vehicleRepository.findAll().stream()
                    .filter(v -> serviceRecordRepository.findAll().stream()
                            .noneMatch(sr -> sr.getVehicle().getId().equals(v.getId()) && 
                                            ("IN_PROGRESS".equals(sr.getStatus()) || "COMPLETED".equals(sr.getStatus()))))
                    .toList());
            model.addAttribute("vehicle", new Vehicle());
            model.addAttribute("customers", customerRepository.findAll());
            model.addAttribute("advisors", serviceAdvisorRepository.findAll());
            return "vehicles";
        }
    }

    @PostMapping("/vehicles/schedule/{id}")
    public String scheduleVehicleService(@PathVariable Long id, 
                                         @RequestParam Long advisorId, 
                                         @RequestParam String issueType,
                                         @RequestParam(required = false) Double workCost) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();
        ServiceAdvisor advisor = serviceAdvisorRepository.findById(advisorId).orElseThrow();
        ServiceRecord record = new ServiceRecord();
        record.setVehicle(vehicle);
        record.setServiceAdvisor(advisor);
        record.setServiceDate(java.time.LocalDate.now());
        record.setStatus(workCost != null ? "COMPLETED" : "IN_PROGRESS");
        record.setIssueType(issueType);
        
        if (workCost != null) {
            List<WorkItem> workItems = new ArrayList<>();
            WorkItem item = new WorkItem();
            item.setDescription(record.getIssueType());
            item.setCost(workCost);
            item.setQuantity(1);
            workItems.add(item);
            record.setWorkItems(workItems);
        }
        
        serviceRecordRepository.save(record);
        return "redirect:/admin/vehicles";
    }

    @GetMapping("/service-advisors")
    public String listServiceAdvisors(Model model) {
        model.addAttribute("advisors", serviceAdvisorRepository.findAll());
        model.addAttribute("advisor", new ServiceAdvisor());
        return "service-advisors";
    }

    @PostMapping("/service-advisors/add")
    public String addServiceAdvisor(@ModelAttribute("advisor") ServiceAdvisor advisor) {
    	
    	Users user=new Users();
    	user.setUserName(advisor.getName());
    	user.setEmail(advisor.getEmail());
    	String encryptedPassword=passwordEncoder.encode(advisor.getPassword());
    	user.setPassword(encryptedPassword);
    	advisor.setPassword(encryptedPassword);
    	user.setRole(Users.Role.ServiceAdvisor);
    	userRepository.save(user);
    	serviceAdvisorRepository.save(advisor);
    	
        return "redirect:/admin/service-advisors";
    }

    @GetMapping("/service-advisors/edit/{id}")
    public String editServiceAdvisor(@PathVariable Long id, Model model) {
        ServiceAdvisor advisor = serviceAdvisorRepository.findById(id).orElseThrow();
        model.addAttribute("advisor", advisor);
        model.addAttribute("advisors", serviceAdvisorRepository.findAll());
        return "service-advisors";
    }

    @GetMapping("/service-advisors/delete/{id}")
    public String deleteServiceAdvisor(@PathVariable Long id, Model model) {
        try {
            ServiceAdvisor advisor = serviceAdvisorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Service Advisor not found with ID: " + id));
            serviceAdvisorRepository.delete(advisor);
            return "redirect:/admin/service-advisors";
        } catch (Exception e) {
            model.addAttribute("error", "Cannot delete advisor: " + e.getMessage());
            model.addAttribute("advisors", serviceAdvisorRepository.findAll());
            model.addAttribute("advisor", new ServiceAdvisor());
            return "service-advisors";
        }
        
        

}

        
        
    
}

