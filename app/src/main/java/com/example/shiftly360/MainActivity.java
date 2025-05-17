package com.example.shiftly360;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.shiftly360.utils.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setupToolbar();
        setupBottomNavigation();

    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton searchButton = findViewById(R.id.search_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);
        
        searchButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });
        
        notificationButton.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });
    }
    
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_shifts) {
                selectedFragment = new ShiftFragment();
            } else if (itemId == R.id.nav_schedule) {
                selectedFragment = new ScheduleFragment();
            } else if (itemId == R.id.nav_communication) {
                selectedFragment = new CommunicationFragment();
            } else if (itemId == R.id.nav_learning) {
                selectedFragment = new LearningFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }
            
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            }
            
            return true;
        });
        
        // Set default fragment
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, new ShiftFragment())
            .commit();
    }

    public void navigateToProfile() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, new ProfileFragment())
            .commit();
        
        // Update bottom navigation selection
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_profile);
    }

    public void recreateActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Not signed in, redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }





} 