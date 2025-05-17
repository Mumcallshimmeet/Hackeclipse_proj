package com.example.shiftly360.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shiftly360.R;
import com.example.shiftly360.adapter.ShiftRequestAdapter;
import com.example.shiftly360.model.ShiftRequest;
import com.example.shiftly360.model.ShiftRequestStatus;
import com.example.shiftly360.service.ShiftManagementService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SupervisorDashboardFragment extends Fragment implements ShiftRequestAdapter.OnRequestActionListener {
    
    private RecyclerView recyclerView;
    private TextView noRequestsText;
    private ProgressBar progressBar;
    private ShiftRequestAdapter adapter;
    private ShiftManagementService shiftManagementService;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supervisor_dashboard, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_shift_requests);
        noRequestsText = view.findViewById(R.id.text_no_requests);
        progressBar = view.findViewById(R.id.progress_bar);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShiftRequestAdapter(this, true);
        recyclerView.setAdapter(adapter);
        
        // Initialize service
        shiftManagementService = new ShiftManagementService();
        
        // Load shift requests
        loadShiftRequests();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        if (shiftManagementService != null) {
            loadShiftRequests();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Remove Firestore listeners
        if (shiftManagementService != null) {
            shiftManagementService.removeListeners();
        }
    }
    
    private void loadShiftRequests() {
        showLoading(true);
        
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showLoading(false);
            showNoRequests(true);
            return;
        }
        
        // Listen for pending requests for this supervisor
        shiftManagementService.listenToSupervisorRequests(
                currentUser.getUid(),
                new ShiftManagementService.OnRequestsLoadedListener() {
                    @Override
                    public void onRequestsLoaded(List<ShiftRequest> requests) {
                        if (isAdded()) {
                            showLoading(false);
                            showNoRequests(requests.isEmpty());
                            adapter.updateRequests(requests);
                        }
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        if (isAdded()) {
                            showLoading(false);
                            showNoRequests(true);
                            Toast.makeText(getContext(), "Error loading requests: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void showNoRequests(boolean show) {
        if (noRequestsText != null) {
            noRequestsText.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    // ShiftRequestAdapter.OnRequestActionListener implementation
    @Override
    public void onApproveClick(ShiftRequest request) {
        showLoading(true);
        
        // Update request status in Firestore
        shiftManagementService.updateRequestStatus(
                request.getId(),
                ShiftRequestStatus.APPROVED,
                task -> {
                    if (isAdded()) {
                        showLoading(false);
                        
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Request approved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to approve request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    
    @Override
    public void onRejectClick(ShiftRequest request) {
        showLoading(true);
        
        // Update request status in Firestore
        shiftManagementService.updateRequestStatus(
                request.getId(),
                ShiftRequestStatus.REJECTED,
                task -> {
                    if (isAdded()) {
                        showLoading(false);
                        
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Request rejected", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to reject request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
} 