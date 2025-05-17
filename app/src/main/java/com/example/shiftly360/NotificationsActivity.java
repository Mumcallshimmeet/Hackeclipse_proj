package com.example.shiftly360;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import com.example.shiftly360.NotificationItem;

/**
 * Activity for displaying notifications to the user
 */
public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private List<NotificationItem> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        setupToolbar();
        setupViews();
        
        if (savedInstanceState != null) {
            // Handle configuration changes if needed
        } else {
            loadNotifications();
        }
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Notifications");
            }
        } catch (Exception e) {
            // Handle toolbar setup error silently
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.notifications_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
        }
    }

    private void loadNotifications() {
        try {
            notifications = new ArrayList<>();
            // Add sample notifications (replace with real data from your backend)
            notifications.add(new NotificationItem("Shift Change Request", 
                "John requested to swap shifts with you for tomorrow", 
                System.currentTimeMillis()));
            notifications.add(new NotificationItem("Schedule Update", 
                "Your schedule for next week has been published", 
                System.currentTimeMillis() - 3600000));
            
            updateUI();
        } catch (Exception e) {
            showError("Error loading notifications");
        }
    }

    private void updateUI() {
        if (notifications == null || notifications.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            NotificationsAdapter adapter = new NotificationsAdapter(notifications);
            recyclerView.setAdapter(adapter);
        }
    }

    private void showError(String message) {
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }
} 