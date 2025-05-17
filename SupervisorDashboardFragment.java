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
import com.example.shiftly360.adapter.ShiftRequestAdapter;

public class SupervisorDashboardFragment extends Fragment implements ShiftRequestAdapter.OnRequestActionListener {
    private RecyclerView pendingRequestsRecycler;
    private ShiftRequestAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_dashboard, container, false);
        pendingRequestsRecycler = view.findViewById(R.id.recycler_shift_requests);
        pendingRequestsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShiftRequestAdapter(this, true);
        pendingRequestsRecycler.setAdapter(adapter);
        return view;
    }

    @Override
    public void onApproveClick(Object request) {
        // TODO: Implement approval logic
    }

    @Override
    public void onRejectClick(Object request) {
        // TODO: Implement rejection logic
    }
} 