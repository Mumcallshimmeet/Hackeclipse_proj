package com.example.shiftly360.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shiftly360.model.ShiftRequest;
import com.example.shiftly360.model.ShiftRequestStatus;
import com.example.shiftly360.model.ShiftRequestType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ShiftManagementService {
    private static final String COLLECTION_SHIFT_REQUESTS = "shift_requests";
    
    private final FirebaseFirestore db;
    private ListenerRegistration employeeRequestListener;
    private ListenerRegistration supervisorRequestListener;

    public interface OnRequestsLoadedListener {
        void onRequestsLoaded(List<ShiftRequest> requests);
        void onError(Exception e);
    }

    public interface OnRequestStatusChangedListener {
        void onRequestStatusChanged(ShiftRequest request);
        void onError(Exception e);
    }

    public ShiftManagementService() {
        this.db = FirebaseFirestore.getInstance();
    }

    // Submit a new shift request
    public void submitShiftRequest(ShiftRequest request, OnCompleteListener<DocumentReference> listener) {
        db.collection(COLLECTION_SHIFT_REQUESTS)
                .add(request)
                .addOnCompleteListener(listener);
    }

    // Approve or reject a shift request
    public void updateRequestStatus(String requestId, ShiftRequestStatus status, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_SHIFT_REQUESTS)
                .document(requestId)
                .update(
                        "status", status,
                        "responseTime", Timestamp.now()
                )
                .addOnCompleteListener(listener);
    }

    // Listen for updates to employee's requests
    public void listenToEmployeeRequests(String employeeId, final OnRequestsLoadedListener listener) {
        if (employeeRequestListener != null) {
            employeeRequestListener.remove();
        }

        employeeRequestListener = db.collection(COLLECTION_SHIFT_REQUESTS)
                .whereEqualTo("employeeId", employeeId)
                .orderBy("requestTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            listener.onError(e);
                            return;
                        }

                        if (snapshots != null) {
                            List<ShiftRequest> requests = snapshots.toObjects(ShiftRequest.class);
                            listener.onRequestsLoaded(requests);
                        }
                    }
                });
    }

    // Listen for pending requests for supervisor
    public void listenToSupervisorRequests(String supervisorId, final OnRequestsLoadedListener listener) {
        if (supervisorRequestListener != null) {
            supervisorRequestListener.remove();
        }

        supervisorRequestListener = db.collection(COLLECTION_SHIFT_REQUESTS)
                .whereEqualTo("supervisorId", supervisorId)
                .whereEqualTo("status", ShiftRequestStatus.PENDING)
                .orderBy("requestTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            listener.onError(e);
                            return;
                        }

                        if (snapshots != null) {
                            List<ShiftRequest> requests = snapshots.toObjects(ShiftRequest.class);
                            listener.onRequestsLoaded(requests);
                        }
                    }
                });
    }

    // Listen for specific request changes
    public void listenToRequestChanges(String requestId, final OnRequestStatusChangedListener listener) {
        db.collection(COLLECTION_SHIFT_REQUESTS)
                .document(requestId)
                .addSnapshotListener(new EventListener<com.google.firebase.firestore.DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable com.google.firebase.firestore.DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            listener.onError(e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            ShiftRequest request = snapshot.toObject(ShiftRequest.class);
                            if (request != null) {
                                listener.onRequestStatusChanged(request);
                            }
                        }
                    }
                });
    }

    // Remove listeners when they're no longer needed
    public void removeListeners() {
        if (employeeRequestListener != null) {
            employeeRequestListener.remove();
            employeeRequestListener = null;
        }
        
        if (supervisorRequestListener != null) {
            supervisorRequestListener.remove();
            supervisorRequestListener = null;
        }
    }
    
    // Get all shift requests for a supervisor (not real-time)
    public Task<QuerySnapshot> getSupervisorRequests(String supervisorId) {
        return db.collection(COLLECTION_SHIFT_REQUESTS)
                .whereEqualTo("supervisorId", supervisorId)
                .orderBy("requestTime", Query.Direction.DESCENDING)
                .get();
    }
} 