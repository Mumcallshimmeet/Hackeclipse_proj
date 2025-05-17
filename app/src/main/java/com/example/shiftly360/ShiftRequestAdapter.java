// app/src/main/java/com/example/shiftly360/ShiftRequestAdapter.java
package com.example.shiftly360;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shiftly360.models.ShiftRequest;
import java.util.ArrayList;
import java.util.List;

public class ShiftRequestAdapter extends RecyclerView.Adapter<ShiftRequestAdapter.ViewHolder> {
    private final List<ShiftRequest> requests = new ArrayList<>();
    private final OnRequestActionListener approveListener;
    private final OnRequestActionListener rejectListener;

    public interface OnRequestActionListener {
        void onAction(ShiftRequest request);
    }

    public ShiftRequestAdapter(OnRequestActionListener approveListener, OnRequestActionListener rejectListener) {
        this.approveListener = approveListener;
        this.rejectListener = rejectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout here
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShiftRequest request = requests.get(position);
        // Bind your data here
        // Set up approve/reject button listeners to call approveListener/rejectListener
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
        int index = requests.indexOf(request);
        if (index != -1) {
            requests.remove(index);
            notifyItemRemoved(index);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your views here
        }
    }
}