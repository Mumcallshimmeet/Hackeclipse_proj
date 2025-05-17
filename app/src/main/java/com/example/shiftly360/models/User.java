package com.example.shiftly360.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private UserRole role;
    private String supervisorId; // ID of the user's supervisor (for normal employees)

    public enum UserRole {
        EMPLOYEE,
        SUPERVISOR
    }

    public User(String userId, String name, String email, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public boolean hasManagementAccess() {
        return role == UserRole.SUPERVISOR;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    public String getSupervisorId() { return supervisorId; }
    public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }
} 