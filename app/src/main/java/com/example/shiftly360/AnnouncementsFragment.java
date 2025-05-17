package com.example.shiftly360;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementsFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Announcement> announcements;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);
        
        recyclerView = view.findViewById(R.id.announcements_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupMockData();

        AnnouncementsAdapter adapter = new AnnouncementsAdapter(announcements);
        recyclerView.setAdapter(adapter);
        
        return view;
    }
    
    private void setupMockData() {
        announcements = new ArrayList<>();
        announcements.add(new Announcement(
            "Important Safety Update",
            "All workers must wear safety helmets in the warehouse area starting next week.",
            "Safety Officer",
            "Today, 09:00 AM"
        ));
        announcements.add(new Announcement(
            "New Break Room Hours",
            "Break room will now be open 24/7 with vending machines available.",
            "Facility Management",
            "Yesterday"
        ));
        announcements.add(new Announcement(
            "Monthly Team Meeting",
            "Don't forget our monthly team meeting this Friday at 3 PM.",
            "Team Lead",
            "2 days ago"
        ));
    }
    
    // Model class
    static class Announcement {
        String title;
        String content;
        String sender;
        String time;
        
        Announcement(String title, String content, String sender, String time) {
            this.title = title;
            this.content = content;
            this.sender = sender;
            this.time = time;
        }
    }
    
    // Adapter class
    class AnnouncementsAdapter extends RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementViewHolder> {
        private final List<Announcement> announcements;
        
        AnnouncementsAdapter(List<Announcement> announcements) {
            this.announcements = announcements;
        }
        
        @NonNull
        @Override
        public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_announcement, parent, false);
            return new AnnouncementViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
            Announcement announcement = announcements.get(position);
            holder.titleText.setText(announcement.title);
            holder.contentText.setText(announcement.content);
            holder.senderText.setText("From: " + announcement.sender);
            holder.timeText.setText(announcement.time);
        }
        
        @Override
        public int getItemCount() {
            return announcements.size();
        }
        
        class AnnouncementViewHolder extends RecyclerView.ViewHolder {
            TextView titleText;
            TextView contentText;
            TextView senderText;
            TextView timeText;
            
            AnnouncementViewHolder(@NonNull View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.announcement_title);
                contentText = itemView.findViewById(R.id.announcement_content);
                senderText = itemView.findViewById(R.id.announcement_sender);
                timeText = itemView.findViewById(R.id.announcement_time);
            }
        }
    }
} 