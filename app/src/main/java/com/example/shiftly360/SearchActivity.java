package com.example.shiftly360;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.LinearLayout;

public class SearchActivity extends AppCompatActivity {
    
    private EditText searchEditText;
    private RecyclerView searchResultsRecycler;
    private LinearLayout noResultsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        searchEditText = findViewById(R.id.search_edit_text);
        searchResultsRecycler = findViewById(R.id.search_results_recycler);
        noResultsContainer = findViewById(R.id.no_results_container);
        Toolbar toolbar = findViewById(R.id.search_toolbar);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set up RecyclerView
        searchResultsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Set up search functionality
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchEditText.getText().toString());
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Request focus and show keyboard after a short delay
        new Handler().postDelayed(() -> {
            searchEditText.requestFocus();
            showKeyboard();
        }, 200);
    }

    private void performSearch(String query) {
        // For demonstration, always show no results
        showNoResults();
    }

    private void showNoResults() {
        searchResultsRecycler.setVisibility(View.GONE);
        noResultsContainer.setVisibility(View.VISIBLE);
    }

    private void showResults() {
        searchResultsRecycler.setVisibility(View.VISIBLE);
        noResultsContainer.setVisibility(View.GONE);
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 