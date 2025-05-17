package com.example.shiftly360.services;

import android.content.Context;
import com.example.shiftly360.models.ShiftRequest;
import com.example.shiftly360.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShiftManagementService {
    private final Context context;
    private final FirebaseFirestore db;
    private final List<ShiftRequestListener> listeners;
    private ListenerRegistration requestListener;

    public interface ShiftRequestListener {
        void onRequestStatusChanged(ShiftRequest request);
        void onNewRequestReceived(ShiftRequest request);
    }

    public ShiftManagementService(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.listeners = new ArrayList<>();
    }

    public void submitRequest(ShiftRequest request) {
        // Create a new document reference with auto-generated ID
        String requestId = db.collection("shift_requests").document().getId();
        request.setRequestId(requestId);
        
        db.collection("shift_requests")
            .document(requestId)
            .set(request)
            .addOnSuccessListener(aVoid -> {
                // Request saved successfully
                for (ShiftRequestListener listener : listeners) {
                    listener.onRequestStatusChanged(request);
                }
            })
            .addOnFailureListener(e -> {
                // Handle the error
                request.setStatus(ShiftRequest.RequestStatus.REJECTED);
                request.setNotes("Failed to submit request: " + e.getMessage());
                for (ShiftRequestListener listener : listeners) {
                    listener.onRequestStatusChanged(request);
                }
            });
    }

    public void approveRequest(String requestId, String notes) {
        updateRequestStatus(requestId, ShiftRequest.RequestStatus.APPROVED, notes);
    }

    public void rejectRequest(String requestId, String notes) {
        updateRequestStatus(requestId, ShiftRequest.RequestStatus.REJECTED, notes);
    }

    private void updateRequestStatus(String requestId, ShiftRequest.RequestStatus status, String notes) {
        db.collection("shift_requests")
            .document(requestId)
            .update(
                "status", status,
                "notes", notes,
                "responseTime", new Date()
            )
            .addOnSuccessListener(aVoid -> {
                // Status updated successfully
                // The listeners will be notified through the snapshot listener
            })
            .addOnFailureListener(e -> {
                // Handle the error
            });
    }

    public void startListeningToRequests(User user) {
        if (requestListener != null) {
            requestListener.remove();
        }

        Query query;
        if (user.hasManagementAccess()) {
            // Supervisors see pending requests assigned to them
            query = db.collection("shift_requests")
                .whereEqualTo("supervisorId", user.getUserId())
                .whereEqualTo("status", ShiftRequest.RequestStatus.PENDING);
        } else {
            // Employees see their own requests
            query = db.collection("shift_requests")
                .whereEqualTo("employeeId", user.getUserId())
                .orderBy("requestTime", Query.Direction.DESCENDING)
                .limit(10); // Limit to last 10 requests
        }

        requestListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                // Handle error
                return;
            }

            if (snapshots != null) {
                for (ShiftRequestListener listener : listeners) {
                    snapshots.getDocumentChanges().forEach(change -> {
                        ShiftRequest request = change.getDocument().toObject(ShiftRequest.class);
                        switch (change.getType()) {
                            case ADDED:
                                if (user.hasManagementAccess()) {
                                    listener.onNewRequestReceived(request);
                                }
                                break;
                            case MODIFIED:
                                listener.onRequestStatusChanged(request);
                                break;
                        }
                    });
                }
            }
        });
    }

    public void stopListening() {
        if (requestListener != null) {
            requestListener.remove();
            requestListener = null;
        }
    }

    public void addListener(ShiftRequestListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(ShiftRequestListener listener) {
        listeners.remove(listener);
    }
} 