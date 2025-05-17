package com.example.shiftly360.models;

import java.util.Date;
import java.util.UUID;

public class ShiftRequest {
    private String requestId;
    private String employeeId;
    private String supervisorId;
    private Type type;
    private RequestStatus status;
    private Date requestTime;
    private Date responseTime;
    private String notes;

    public enum Type {
        START_SHIFT,
        END_SHIFT,
        START_BREAK,
        END_BREAK
    }

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    // Required for Firestore
    public ShiftRequest() {
    }

    public ShiftRequest(String employeeId, Type type) {
        this.requestId = UUID.randomUUID().toString();
        this.employeeId = employeeId;
        this.type = type;
        this.status = RequestStatus.PENDING;
        this.requestTime = new Date();
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getSupervisorId() { return supervisorId; }
    public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }
    
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { 
        this.status = status;
        if (status != RequestStatus.PENDING) {
            this.responseTime = new Date();
        }
    }
    
    public Date getRequestTime() { return requestTime; }
    public void setRequestTime(Date requestTime) { this.requestTime = requestTime; }
    
    public Date getResponseTime() { return responseTime; }
    public void setResponseTime(Date responseTime) { this.responseTime = responseTime; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
} 