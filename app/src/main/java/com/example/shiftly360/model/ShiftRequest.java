package com.example.shiftly360.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

public class ShiftRequest {
    @DocumentId
    private String id;
    private String employeeId;
    private String employeeName;
    private String supervisorId;
    private Timestamp requestTime;
    private Timestamp responseTime;
    private ShiftRequestType requestType;
    private ShiftRequestStatus status;
    private String notes;

    // Required empty constructor for Firestore
    public ShiftRequest() {
    }

    public ShiftRequest(String employeeId, String employeeName, String supervisorId, 
                        ShiftRequestType requestType, String notes) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.supervisorId = supervisorId;
        this.requestTime = Timestamp.now();
        this.requestType = requestType;
        this.status = ShiftRequestStatus.PENDING;
        this.notes = notes;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }

    public Timestamp getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Timestamp responseTime) {
        this.responseTime = responseTime;
    }

    public ShiftRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(ShiftRequestType requestType) {
        this.requestType = requestType;
    }

    public ShiftRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ShiftRequestStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
} 