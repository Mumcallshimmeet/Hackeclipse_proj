package com.example.shiftly360.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shiftly360.R;
import com.example.shiftly360.model.ShiftRequest;
import com.example.shiftly360.model.ShiftRequestStatus;
import com.example.shiftly360.model.ShiftRequestType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShiftRequestAdapter extends RecyclerView.Adapter<ShiftRequestAdapter.ViewHolder> {
    private List<ShiftRequest> requests;
    private final OnRequestActionListener listener;
    private final boolean showActions;

    public interface OnRequestActionListener {
        void onApproveClick(ShiftRequest request);
        void onRejectClick(ShiftRequest request);
    }

    public ShiftRequestAdapter(OnRequestActionListener listener, boolean showActions) {
        this.requests = new ArrayList<>();
        this.listener = listener;
        this.showActions = showActions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shift_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShiftRequest request = requests.get(position);
        
        // Set employee name
        holder.employeeNameText.setText(request.getEmployeeName());
        
        // Set request type with formatted display
        String requestTypeDisplay = formatRequestType(request.getRequestType());
        holder.requestTypeText.setText(requestTypeDisplay);
        
        // Format and set request time
        String formattedTime = formatTimestamp(new Date(request.getRequestTime().getSeconds() * 1000));
        holder.requestTimeText.setText(formattedTime);
        
        // Set notes (if any)
        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            holder.notesText.setText(request.getNotes());
            holder.notesText.setVisibility(View.VISIBLE);
        } else {
            holder.notesText.setVisibility(View.GONE);
        }
        
        // Show or hide action buttons based on status and settings
        if (request.getStatus() == ShiftRequestStatus.PENDING && showActions) {
            holder.actionsLayout.setVisibility(View.VISIBLE);
            holder.statusText.setVisibility(View.GONE);
            
            holder.approveButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApproveClick(request);
                }
            });
            
            holder.rejectButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRejectClick(request);
                }
            });
        } else {
            holder.actionsLayout.setVisibility(View.GONE);
            holder.statusText.setVisibility(View.VISIBLE);
            holder.statusText.setText(request.getStatus().name());
            
            // Set status color
            int statusColor;
            switch (request.getStatus()) {
                case APPROVED:
                    statusColor = holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark);
                    break;
                case REJECTED:
                    statusColor = holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark);
                    break;
                default:
                    statusColor = holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray);
            }
            holder.statusText.setTextColor(statusColor);
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void updateRequests(List<ShiftRequest> newRequests) {
        this.requests = newRequests;
        notifyDataSetChanged();
    }
    
    private String formatRequestType(ShiftRequestType type) {
        if (type == null) return "";
        
        // Convert enum to readable format (e.g., START_SHIFT -> "Start Shift")
        String typeStr = type.name().replace("_", " ");
        String[] words = typeStr.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                result.append(word.charAt(0))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    private String formatTimestamp(Date date) {
        return DateFormat.format("MMM dd, yyyy hh:mm a", date).toString();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView employeeNameText;
        final TextView requestTypeText;
        final TextView requestTimeText;
        final TextView notesText;
        final LinearLayout actionsLayout;
        final Button approveButton;
        final Button rejectButton;
        final TextView statusText;

        ViewHolder(View view) {
            super(view);
            employeeNameText = view.findViewById(R.id.text_employee_name);
            requestTypeText = view.findViewById(R.id.text_request_type);
            requestTimeText = view.findViewById(R.id.text_request_time);
            notesText = view.findViewById(R.id.text_notes);
            actionsLayout = view.findViewById(R.id.layout_actions);
            approveButton = view.findViewById(R.id.btn_approve);
            rejectButton = view.findViewById(R.id.btn_reject);
            statusText = view.findViewById(R.id.text_status);
        }
    }
} 