package com.example.shiftly360;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shiftly360.models.ShiftRequest;
import com.example.shiftly360.models.User;
import com.example.shiftly360.services.ShiftManagementService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class SupervisorDashboardFragment extends Fragment implements ShiftManagementService.ShiftRequestListener {
    private RecyclerView pendingRequestsRecycler;
    private ShiftRequestAdapter adapter;
    private ShiftManagementService shiftManagementService;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supervisor_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        pendingRequestsRecycler = view.findViewById(R.id.recycler_shift_requests);
        pendingRequestsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ShiftRequestAdapter(this::showApprovalDialog, this::showRejectionDialog);
        pendingRequestsRecycler.setAdapter(adapter);

        // Initialize ShiftManagementService
        shiftManagementService = new ShiftManagementService(requireContext());
        shiftManagementService.addListener(this);

        // Start listening for requests
        if (currentUser != null && currentUser.hasManagementAccess()) {
            shiftManagementService.startListeningToRequests(currentUser);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        shiftManagementService.stopListening();
        shiftManagementService.removeListener(this);
    }

    @Override
    public void onRequestStatusChanged(ShiftRequest request) {
        // Not needed for supervisor view
    }

    @Override
    public void onNewRequestReceived(ShiftRequest request) {
        requireActivity().runOnUiThread(() -> {
            adapter.addRequest(request);
        });
    }

    private void showApprovalDialog(ShiftRequest request) {
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_request_notes, null);
        TextInputEditText notesInput = dialogView.findViewById(R.id.notes_input);

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Approve Request")
            .setView(dialogView)
            .setPositiveButton("Approve", (dialog, which) -> {
                String notes = notesInput.getText().toString();
                shiftManagementService.approveRequest(request.getRequestId(), notes);
                adapter.removeRequest(request);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showRejectionDialog(ShiftRequest request) {
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_request_notes, null);
        TextInputEditText notesInput = dialogView.findViewById(R.id.notes_input);

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Reject Request")
            .setView(dialogView)
            .setPositiveButton("Reject", (dialog, which) -> {
                String notes = notesInput.getText().toString();
                shiftManagementService.rejectRequest(request.getRequestId(), notes);
                adapter.removeRequest(request);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user.hasManagementAccess() && shiftManagementService != null) {
            shiftManagementService.startListeningToRequests(user);
        }
    }
} 