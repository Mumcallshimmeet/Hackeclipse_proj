package com.example.shiftly360.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.example.shiftly360.model.ShiftRequestType;
import com.example.shiftly360.service.ShiftManagementService;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.Arrays;
import java.util.List;

public class ShiftRequestFragment extends Fragment {

    private MaterialCardView requestFormCard;
    private Spinner requestTypeSpinner;
    private EditText supervisorIdInput;
    private EditText notesInput;
    private Button submitButton;
    
    private RecyclerView recyclerView;
    private TextView noRequestsText;
    private ProgressBar progressBar;
    
    private ShiftRequestAdapter adapter;
    private ShiftManagementService shiftManagementService;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shift_request, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize service
        shiftManagementService = new ShiftManagementService();
        
        // Initialize form views
        requestFormCard = view.findViewById(R.id.card_request_form);
        requestTypeSpinner = view.findViewById(R.id.spinner_request_type);
        supervisorIdInput = view.findViewById(R.id.input_supervisor_id);
        notesInput = view.findViewById(R.id.input_notes);
        submitButton = view.findViewById(R.id.btn_submit_request);
        
        // Set up spinner with shift request types
        setupRequestTypeSpinner();
        
        // Initialize list views
        recyclerView = view.findViewById(R.id.recycler_my_requests);
        noRequestsText = view.findViewById(R.id.text_no_requests);
        progressBar = view.findViewById(R.id.progress_bar);
        
        // Setup RecyclerView for employee's requests
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShiftRequestAdapter(null, false); // No actions for employee view
        recyclerView.setAdapter(adapter);
        
        // Set up submit button click listener
        submitButton.setOnClickListener(v -> submitShiftRequest());
        
        // Load employee's previous requests
        loadMyRequests();
    }
    
    private void setupRequestTypeSpinner() {
        // Create adapter for spinner using ShiftRequestType values
        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getFormattedRequestTypes());
        
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requestTypeSpinner.setAdapter(spinnerAdapter);
    }
    
    private List<String> getFormattedRequestTypes() {
        ShiftRequestType[] types = ShiftRequestType.values();
        String[] formattedTypes = new String[types.length];
        
        for (int i = 0; i < types.length; i++) {
            String typeStr = types[i].name().replace("_", " ");
            String[] words = typeStr.split(" ");
            StringBuilder result = new StringBuilder();
            
            for (String word : words) {
                if (word.length() > 0) {
                    result.append(word.charAt(0))
                          .append(word.substring(1).toLowerCase())
                          .append(" ");
                }
            }
            
            formattedTypes[i] = result.toString().trim();
        }
        
        return Arrays.asList(formattedTypes);
    }
    
    private ShiftRequestType getSelectedRequestType() {
        int position = requestTypeSpinner.getSelectedItemPosition();
        return ShiftRequestType.values()[position];
    }
    
    private void submitShiftRequest() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please sign in to submit requests", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String supervisorId = supervisorIdInput.getText().toString().trim();
        if (supervisorId.isEmpty()) {
            supervisorIdInput.setError("Supervisor ID is required");
            return;
        }
        
        String notes = notesInput.getText().toString().trim();
        ShiftRequestType requestType = getSelectedRequestType();
        
        // Show loading state
        setFormEnabled(false);
        showLoading(true);
        
        // Create the request
        ShiftRequest request = new ShiftRequest(
                currentUser.getUid(),
                currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Employee",
                supervisorId,
                requestType,
                notes
        );
        
        // Submit the request to Firestore
        shiftManagementService.submitShiftRequest(request, task -> {
            if (isAdded()) {
                setFormEnabled(true);
                showLoading(false);
                
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Request submitted successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                    loadMyRequests(); // Reload the list
                } else {
                    Toast.makeText(getContext(), "Failed to submit request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void loadMyRequests() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showNoRequests(true);
            return;
        }
        
        showLoading(true);
        
        // Listen for this employee's requests
        shiftManagementService.listenToEmployeeRequests(
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
    
    private void setFormEnabled(boolean enabled) {
        requestTypeSpinner.setEnabled(enabled);
        supervisorIdInput.setEnabled(enabled);
        notesInput.setEnabled(enabled);
        submitButton.setEnabled(enabled);
    }
    
    private void clearForm() {
        requestTypeSpinner.setSelection(0);
        supervisorIdInput.setText("");
        notesInput.setText("");
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
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
    
    @Override
    public void onResume() {
        super.onResume();
        if (shiftManagementService != null) {
            loadMyRequests();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (shiftManagementService != null) {
            shiftManagementService.removeListeners();
        }
    }
} 