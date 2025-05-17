package com.example.shiftly360;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LearningFragment extends Fragment {

    private RecyclerView lessonsRecyclerView;
    private RecyclerView certificatesRecyclerView;
    private TextView progressText;
    private ProgressBar progressBar;
    private ImageButton audioLessonButton;
    
    // Mock data
    private List<Lesson> dailyLessons;
    private List<Certificate> certificates;
    private int completedLessons = 3;
    private int totalLessons = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learning, container, false);
        
        // Initialize views
        lessonsRecyclerView = view.findViewById(R.id.lessons_recycler_view);
        certificatesRecyclerView = view.findViewById(R.id.certificates_recycler_view);
        progressText = view.findViewById(R.id.progress_text);
        progressBar = view.findViewById(R.id.progress_bar);
        audioLessonButton = view.findViewById(R.id.audio_lesson_button);
        
        // Set up RecyclerViews
        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        certificatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        
        // Initialize mock data
        setupMockData();
        
        // Set up progress
        updateProgress();
        
        // Set up audio lesson button
        audioLessonButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Playing daily audio lesson...", Toast.LENGTH_SHORT).show();
            // In a real app, this would start playing an audio lesson
        });
        
        // Set adapters
        lessonsRecyclerView.setAdapter(new LessonAdapter(dailyLessons));
        certificatesRecyclerView.setAdapter(new CertificateAdapter(certificates));
        
        return view;
    }
    
    private void setupMockData() {
        // Mock daily lessons
        dailyLessons = new ArrayList<>();
        dailyLessons.add(new Lesson("Safety First", "Learn about workplace safety protocols", true));
        dailyLessons.add(new Lesson("Efficient Stacking", "Techniques for efficient warehouse stacking", true));
        dailyLessons.add(new Lesson("Equipment Care", "How to properly maintain workplace equipment", true));
        dailyLessons.add(new Lesson("Communication Skills", "Effective workplace communication", false));
        dailyLessons.add(new Lesson("Time Management", "Tips for better time management", false));
        
        // Mock certificates
        certificates = new ArrayList<>();
        certificates.add(new Certificate("Safety Champion", "Completed all safety modules", "2023-05-15"));
        certificates.add(new Certificate("Equipment Expert", "Mastered equipment handling", "2023-05-20"));
        certificates.add(new Certificate("Team Player", "Excellent collaboration skills", "2023-06-01"));
    }
    
    private void updateProgress() {
        progressText.setText(String.format("Learning Progress: %d/%d", completedLessons, totalLessons));
        progressBar.setMax(totalLessons);
        progressBar.setProgress(completedLessons);
    }
    
    // Model classes
    static class Lesson {
        String title;
        String description;
        boolean completed;
        
        public Lesson(String title, String description, boolean completed) {
            this.title = title;
            this.description = description;
            this.completed = completed;
        }
    }
    
    static class Certificate {
        String title;
        String description;
        String dateEarned;
        
        public Certificate(String title, String description, String dateEarned) {
            this.title = title;
            this.description = description;
            this.dateEarned = dateEarned;
        }
    }
    
    // Adapters
    class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
        
        private List<Lesson> lessons;
        
        public LessonAdapter(List<Lesson> lessons) {
            this.lessons = lessons;
        }
        
        @NonNull
        @Override
        public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_lesson, parent, false);
            return new LessonViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
            // Bind data in the real implementation
            Lesson lesson = lessons.get(position);
            // In a real implementation, we would set the views here
        }
        
        @Override
        public int getItemCount() {
            return lessons.size();
        }
        
        class LessonViewHolder extends RecyclerView.ViewHolder {
            public LessonViewHolder(@NonNull View itemView) {
                super(itemView);
                // Initialize views in the real implementation
            }
        }
    }
    
    class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {
        
        private List<Certificate> certificates;
        
        public CertificateAdapter(List<Certificate> certificates) {
            this.certificates = certificates;
        }
        
        @NonNull
        @Override
        public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_certificate, parent, false);
            return new CertificateViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull CertificateViewHolder holder, int position) {
            // Bind data in the real implementation
            Certificate certificate = certificates.get(position);
            // In a real implementation, we would set the views here
        }
        
        @Override
        public int getItemCount() {
            return certificates.size();
        }
        
        class CertificateViewHolder extends RecyclerView.ViewHolder {
            public CertificateViewHolder(@NonNull View itemView) {
                super(itemView);
                // Initialize views in the real implementation
            }
        }
    }
} 