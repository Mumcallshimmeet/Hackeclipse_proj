package com.example.shiftly360;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.shiftly360.models.ShiftRequest;
import com.example.shiftly360.models.User;
import com.example.shiftly360.services.ShiftManagementService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShiftFragment extends Fragment implements ShiftTimerService.ShiftUpdateListener, ShiftManagementService.ShiftRequestListener {
    private TextView statusText;
    private TextView timeText;
    private Button startShiftBtn;
    private Button endShiftBtn;
    private Button breakBtn;
    
    private boolean isOnShift = false;
    private boolean isOnBreak = false;
    private long shiftStartTime = 0;
    private long breakStartTime = 0;
    private long totalWorkTime = 0;
    
    private ShiftTimerService timerService;
    private ShiftManagementService shiftManagementService;
    private boolean isBound = false;
    private boolean shouldStartShift = false;
    private User currentUser;
    private ShiftRequest pendingRequest = null;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ShiftTimerService.LocalBinder binder = (ShiftTimerService.LocalBinder) service;
            timerService = binder.getService();
            timerService.setListener(ShiftFragment.this);
            isBound = true;
            
            if (shouldStartShift) {
                shouldStartShift = false;
                startShiftInternal();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timerService = null;
            isBound = false;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAndBindService();
        shiftManagementService = new ShiftManagementService(requireContext());
        shiftManagementService.addListener(this);
    }

    private void startAndBindService() {
        if (getActivity() != null) {
            try {
                Intent intent = new Intent(getActivity(), ShiftTimerService.class);
                getActivity().startService(intent);
                boolean bound = getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
                if (!bound) {
                    Toast.makeText(getActivity(), "Could not bind to shift service", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error starting shift service", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shift, container, false);
        
        // Initialize views
        statusText = view.findViewById(R.id.status_text);
        timeText = view.findViewById(R.id.time_text);
        startShiftBtn = view.findViewById(R.id.start_shift_button);
        endShiftBtn = view.findViewById(R.id.end_shift_button);
        breakBtn = view.findViewById(R.id.break_button);
        Button sosButton = view.findViewById(R.id.sos_button);
        
        // Set up listeners
        startShiftBtn.setOnClickListener(v -> startShift());
        endShiftBtn.setOnClickListener(v -> endShift());
        breakBtn.setOnClickListener(v -> handleBreak());
        sosButton.setOnClickListener(v -> handleSosClick());
        
        // Initial UI setup
        updateUI();
        
        return view;
    }
    
    private void startShift() {
        if (!isOnShift) {
            if (isBound && timerService != null) {
                startShiftInternal();
            } else {
                shouldStartShift = true;
                startAndBindService();
            }
        }
    }
    
    private void startShiftInternal() {
        try {
            ShiftRequest request = new ShiftRequest(currentUser.getUserId(), ShiftRequest.Type.START_SHIFT);
            shiftManagementService.submitRequest(request);
            pendingRequest = request;
            updateUI();
            Toast.makeText(getContext(), "Request sent to supervisor. Waiting for approval...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error submitting shift request", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void endShift() {
        if (isOnShift && isBound && timerService != null) {
            try {
                ShiftRequest request = new ShiftRequest(currentUser.getUserId(), ShiftRequest.Type.END_SHIFT);
                shiftManagementService.submitRequest(request);
                pendingRequest = request;
                updateUI();
                Toast.makeText(getContext(), "End shift request submitted. Waiting for approval.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error submitting end shift request", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void handleBreak() {
        if (isOnShift) {
            try {
                ShiftRequest.Type breakType = isOnBreak ? ShiftRequest.Type.END_BREAK : ShiftRequest.Type.START_BREAK;
                ShiftRequest request = new ShiftRequest(currentUser.getUserId(), breakType);
                shiftManagementService.submitRequest(request);
                pendingRequest = request;
                updateUI();
                Toast.makeText(getContext(), 
                    isOnBreak ? "Break end request submitted" : "Break request submitted", 
                    Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error submitting break request", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "You must start a shift first", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onShiftTimeUpdate(long startTime, long duration) {
        if (isAdded() && isOnShift) {
            shiftStartTime = startTime;
            updateUI();
        }
    }

    @Override
    public void onRequestStatusChanged(ShiftRequest request) {
        if (pendingRequest != null && request.getRequestId().equals(pendingRequest.getRequestId())) {
            switch (request.getStatus()) {
                case APPROVED:
                    handleRequestApproved(request);
                    break;
                case REJECTED:
                    handleRequestRejected(request);
                    break;
            }
            pendingRequest = null;
            updateUI();
        }
    }

    private void handleRequestApproved(ShiftRequest request) {
        switch (request.getType()) {
            case START_SHIFT:
                isOnShift = true;
                shiftStartTime = System.currentTimeMillis();
                if (timerService != null) timerService.startShift();
                Toast.makeText(getContext(), "Shift request approved. Your shift has started!", Toast.LENGTH_SHORT).show();
                break;
            case END_SHIFT:
                isOnShift = false;
                if (isOnBreak) isOnBreak = false;
                if (timerService != null) timerService.stopShift();
                totalWorkTime = System.currentTimeMillis() - shiftStartTime;
                Toast.makeText(getContext(), "Shift ended", Toast.LENGTH_SHORT).show();
                break;
            case START_BREAK:
                isOnBreak = true;
                breakStartTime = System.currentTimeMillis();
                Toast.makeText(getContext(), "Break started", Toast.LENGTH_SHORT).show();
                break;
            case END_BREAK:
                isOnBreak = false;
                Toast.makeText(getContext(), "Break ended", Toast.LENGTH_SHORT).show();
                break;
        }
        updateUI();
    }

    private void handleRequestRejected(ShiftRequest request) {
        String message = "Request rejected";
        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            message += ": " + request.getNotes();
        }
        
        // Reset any pending state for rejected requests
        switch (request.getType()) {
            case START_SHIFT:
                isOnShift = false;
                shiftStartTime = 0;
                break;
            case END_SHIFT:
                // Keep the shift active if end request is rejected
                break;
            case START_BREAK:
                isOnBreak = false;
                breakStartTime = 0;
                break;
            case END_BREAK:
                // Keep break active if end request is rejected
                break;
        }
        
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        updateUI();
    }
    
    private void updateUI() {
        if (pendingRequest != null) {
            String waitingMessage = "Waiting for approval...";
            switch (pendingRequest.getType()) {
                case START_SHIFT:
                    waitingMessage = "Waiting for shift approval...";
                    break;
                case END_SHIFT:
                    waitingMessage = "Waiting for end shift approval...";
                    break;
                case START_BREAK:
                    waitingMessage = "Waiting for break approval...";
                    break;
                case END_BREAK:
                    waitingMessage = "Waiting for break end approval...";
                    break;
            }
            statusText.setText(waitingMessage);
            startShiftBtn.setEnabled(false);
            endShiftBtn.setEnabled(false);
            breakBtn.setEnabled(false);
            timeText.setText("");
        } else if (isOnShift) {
            statusText.setText(isOnBreak ? "On Break" : "On Shift");
            endShiftBtn.setEnabled(true);
            startShiftBtn.setEnabled(false);
            breakBtn.setText(isOnBreak ? R.string.return_from_break : R.string.take_break);
            breakBtn.setEnabled(true);
            
            if (shiftStartTime > 0) {
                long currentDuration = System.currentTimeMillis() - shiftStartTime;
                String startTimeStr = formatTime(shiftStartTime);
                String durationStr = formatDuration(currentDuration);
                timeText.setText(String.format("Started at: %s (%s)", startTimeStr, durationStr));
            }
        } else {
            statusText.setText("Not on shift");
            startShiftBtn.setEnabled(true);
            endShiftBtn.setEnabled(false);
            breakBtn.setEnabled(false);
            
            if (totalWorkTime > 0) {
                timeText.setText("Last shift: " + formatDuration(totalWorkTime));
            } else {
                timeText.setText("");
            }
        }
    }
    
    private String formatTime(long timeInMillis) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timeInMillis));
    }
    
    private String formatDuration(long durationInMillis) {
        long seconds = durationInMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes %= 60;
        
        return String.format(Locale.getDefault(), "%dh %02dm", hours, minutes);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isBound) {
            startAndBindService();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isOnShift && isBound) {
            unbindService();
        }
    }

    @Override
    public void onDestroy() {
        unbindService();
        if (shiftManagementService != null) {
            shiftManagementService.removeListener(this);
        }
        super.onDestroy();
    }

    private void unbindService() {
        if (getActivity() != null && isBound) {
            try {
                getActivity().unbindService(connection);
            } catch (Exception e) {
                // Handle unbind error silently
            }
            isBound = false;
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void handleSosClick() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE);
        String emergencyContact = prefs.getString("emergency_contact_number", "");

        if (emergencyContact.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                .setTitle("No Emergency Contact")
                .setMessage("Please set up an emergency contact in your profile first.")
                .setPositiveButton("Go to Profile", (dialog, which) -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigateToProfile();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
            return;
        }

        new AlertDialog.Builder(requireContext())
            .setTitle("SOS Alert")
            .setMessage("Are you sure you want to call your emergency contact?")
            .setPositiveButton("Call", (dialog, which) -> {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            REQUEST_CALL_PERMISSION);
                } else {
                    makeEmergencyCall(emergencyContact);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static final int REQUEST_CALL_PERMISSION = 1;

    private void makeEmergencyCall(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(getContext(), "Failed to make emergency call", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SharedPreferences prefs = requireActivity().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE);
                String emergencyContact = prefs.getString("emergency_contact_number", "");
                if (!emergencyContact.isEmpty()) {
                    makeEmergencyCall(emergencyContact);
                }
            } else {
                Toast.makeText(getContext(), "Permission denied to make emergency calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNewRequestReceived(ShiftRequest request) {
        // As an employee, we don't need to handle new requests
    }
} 