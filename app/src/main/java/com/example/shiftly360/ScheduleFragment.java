package com.example.shiftly360;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    
    // Mock data
    private List<Shift> myShifts;
    private List<Shift> availableShifts;
    private List<Worker> preferredWorkers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        
        // Initialize views
        tabLayout = view.findViewById(R.id.tab_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        fab = view.findViewById(R.id.fab);
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize mock data
        setupMockData();
        
        // Set up tabs
        tabLayout.addTab(tabLayout.newTab().setText(R.string.my_schedule));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.available_shifts));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.preferred_workers));
        
        // Set up tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateRecyclerViewForTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
        
        // Set up FAB
        fab.setOnClickListener(v -> {
            int selectedTabPosition = tabLayout.getSelectedTabPosition();
            
            switch (selectedTabPosition) {
                case 0: // My Schedule
                    Toast.makeText(getContext(), "Request shift swap", Toast.LENGTH_SHORT).show();
                    break;
                case 1: // Available Shifts
                    Toast.makeText(getContext(), "Apply for shift", Toast.LENGTH_SHORT).show();
                    break;
                case 2: // Preferred Workers
                    Toast.makeText(getContext(), "Quick hire", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        
        // Set initial tab
        updateRecyclerViewForTab(0);
        
        return view;
    }
    
    private void setupMockData() {
        // Mock shifts for user
        myShifts = new ArrayList<>();
        myShifts.add(new Shift("Morning Shift", "2023-06-01", "08:00", "16:00", "Warehouse A"));
        myShifts.add(new Shift("Evening Shift", "2023-06-02", "16:00", "00:00", "Warehouse B"));
        myShifts.add(new Shift("Morning Shift", "2023-06-05", "08:00", "16:00", "Warehouse A"));
        
        // Mock available shifts
        availableShifts = new ArrayList<>();
        availableShifts.add(new Shift("Night Shift", "2023-06-03", "00:00", "08:00", "Warehouse C"));
        availableShifts.add(new Shift("Evening Shift", "2023-06-04", "16:00", "00:00", "Warehouse A"));
        availableShifts.add(new Shift("Morning Shift", "2023-06-06", "08:00", "16:00", "Warehouse B"));
        
        // Mock preferred workers
        preferredWorkers = new ArrayList<>();
        preferredWorkers.add(new Worker("John Doe", 4.5f, "Warehouse Operations"));
        preferredWorkers.add(new Worker("Jane Smith", 4.8f, "Inventory Management"));
        preferredWorkers.add(new Worker("Robert Johnson", 4.2f, "Logistics"));
    }
    
    private void updateRecyclerViewForTab(int tabPosition) {
        switch (tabPosition) {
            case 0: // My Schedule
                recyclerView.setAdapter(new ShiftAdapter(myShifts));
                fab.setImageResource(android.R.drawable.ic_menu_share);
                fab.setContentDescription("Request shift swap");
                break;
            case 1: // Available Shifts
                recyclerView.setAdapter(new ShiftAdapter(availableShifts));
                fab.setImageResource(android.R.drawable.ic_menu_add);
                fab.setContentDescription("Apply for shift");
                break;
            case 2: // Preferred Workers
                recyclerView.setAdapter(new WorkerAdapter(preferredWorkers));
                fab.setImageResource(android.R.drawable.ic_menu_call);
                fab.setContentDescription("Quick hire");
                break;
        }
    }
    
    // Model classes
    static class Shift {
        String name;
        String date;
        String startTime;
        String endTime;
        String location;
        
        public Shift(String name, String date, String startTime, String endTime, String location) {
            this.name = name;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.location = location;
        }
    }
    
    static class Worker {
        String name;
        float rating;
        String specialty;
        
        public Worker(String name, float rating, String specialty) {
            this.name = name;
            this.rating = rating;
            this.specialty = specialty;
        }
    }
    
    // Adapters
    class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ShiftViewHolder> {
        
        private List<Shift> shifts;
        
        public ShiftAdapter(List<Shift> shifts) {
            this.shifts = shifts;
        }
        
        @NonNull
        @Override
        public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shift, parent, false);
            return new ShiftViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ShiftViewHolder holder, int position) {
            Shift shift = shifts.get(position);
            holder.shiftDate.setText(shift.date);
            holder.shiftTime.setText(String.format("%s - %s", shift.startTime, shift.endTime));
            holder.shiftLocation.setText(shift.location);
            holder.shiftRole.setText(shift.name);
        }
        
        @Override
        public int getItemCount() {
            return shifts.size();
        }
        
        class ShiftViewHolder extends RecyclerView.ViewHolder {
            TextView shiftDate;
            TextView shiftTime;
            TextView shiftLocation;
            TextView shiftRole;

            public ShiftViewHolder(@NonNull View itemView) {
                super(itemView);
                shiftDate = itemView.findViewById(R.id.shiftDate);
                shiftTime = itemView.findViewById(R.id.shiftTime);
                shiftLocation = itemView.findViewById(R.id.shiftLocation);
                shiftRole = itemView.findViewById(R.id.shiftRole);
            }
        }
    }
    
    class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {
        
        private List<Worker> workers;
        
        public WorkerAdapter(List<Worker> workers) {
            this.workers = workers;
        }
        
        @NonNull
        @Override
        public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_worker, parent, false);
            return new WorkerViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
            // Bind data in the real implementation
        }
        
        @Override
        public int getItemCount() {
            return workers.size();
        }
        
        class WorkerViewHolder extends RecyclerView.ViewHolder {
            public WorkerViewHolder(@NonNull View itemView) {
                super(itemView);
                // Initialize views in the real implementation
            }
        }
    }
} 