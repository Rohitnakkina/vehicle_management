package com.techm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class ServiceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Vehicle vehicle;
    @ManyToOne
    private ServiceAdvisor serviceAdvisor;
    private LocalDate serviceDate;
    private String status; // DUE, IN_PROGRESS, COMPLETED
    private String issueType; // New field
    @OneToMany(cascade = CascadeType.ALL)
    private List<WorkItem> workItems;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public ServiceAdvisor getServiceAdvisor() { return serviceAdvisor; }
    public void setServiceAdvisor(ServiceAdvisor serviceAdvisor) { this.serviceAdvisor = serviceAdvisor; }
    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }
    public List<WorkItem> getWorkItems() { return workItems; }
    public void setWorkItems(List<WorkItem> workItems) { this.workItems = workItems; }
}