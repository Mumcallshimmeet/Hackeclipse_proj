package com.example.shiftly360.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shiftly360.R;
import com.example.shiftly360.models.ShiftRequest;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShiftRequestAdapter extends RecyclerView.Adapter<ShiftRequestAdapter.ViewHolder> {
    private List<ShiftRequest> requests;
    private final OnRequestActionListener approveListener;
    private final OnRequestActionListener rejectListener;
    private final SimpleDateFormat timeFormat;

    public interface OnRequestActionListener {
        void onAction(ShiftRequest request);
    }

    public ShiftRequestAdapter(OnRequestActionListener approveListener, OnRequestActionListener rejectListener) {
        this.requests = new ArrayList<>();
        this.approveListener = approveListener;
        this.rejectListener = rejectListener;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
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

        holder.employeeName.setText(request.getEmployeeId()); // TODO: Get actual name from User service
        holder.requestTime.setText(timeFormat.format(request.getRequestTime()));

        String requestType = "";
        switch (request.getType()) {
            case START_SHIFT:
                requestType = "Start Shift";
                break;
            case END_SHIFT:
                requestType = "End Shift";
                break;
            case START_BREAK:
                requestType = "Start Break";
                break;
            case END_BREAK:
                requestType = "End Break";
                break;
        }
        holder.requestType.setText(requestType);

        holder.approveButton.setOnClickListener(v -> approveListener.onAction(request));
        holder.rejectButton.setOnClickListener(v -> rejectListener.onAction(request));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void addRequest(ShiftRequest request) {
        requests.add(request);
        notifyItemInserted(requests.size() - 1);
    }

    public void removeRequest(ShiftRequest request) {
        int position = requests.indexOf(request);
        if (position != -1) {
            requests.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView employeeName;
        TextView requestTime;
        TextView requestType;
        MaterialButton approveButton;
        MaterialButton rejectButton;

        ViewHolder(View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.text_employee_name);
            requestTime = itemView.findViewById(R.id.text_request_time);
            requestType = itemView.findViewById(R.id.text_request_time);
            approveButton = itemView.findViewById(R.id.btn_approve);
            rejectButton = itemView.findViewById(R.id.btn_reject);
        }
    }
}