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

public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Chat> chats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        
        recyclerView = view.findViewById(R.id.chats_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupMockData();

        ChatsAdapter adapter = new ChatsAdapter(chats);
        recyclerView.setAdapter(adapter);
        
        return view;
    }
    
    private void setupMockData() {
        chats = new ArrayList<>();
        chats.add(new Chat("John Smith", "Can you cover my shift tomorrow?", "10:30 AM", 1));
        chats.add(new Chat("Safety Team", "New safety guidelines posted", "Yesterday", 0));
        chats.add(new Chat("HR Department", "Your leave request was approved", "Yesterday", 0));
    }

    static class Chat {
        String name;
        String lastMessage;
        String time;
        int unreadCount;
        
        Chat(String name, String lastMessage, String time, int unreadCount) {
            this.name = name;
            this.lastMessage = lastMessage;
            this.time = time;
            this.unreadCount = unreadCount;
        }
    }

    class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {
        private final List<Chat> chats;
        
        ChatsAdapter(List<Chat> chats) {
            this.chats = chats;
        }
        
        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat, parent, false);
            return new ChatViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            Chat chat = chats.get(position);
            holder.nameText.setText(chat.name);
            holder.messageText.setText(chat.lastMessage);
            holder.timeText.setText(chat.time);
            
            if (chat.unreadCount > 0) {
                holder.unreadCount.setVisibility(View.VISIBLE);
                holder.unreadCount.setText(String.valueOf(chat.unreadCount));
            } else {
                holder.unreadCount.setVisibility(View.GONE);
            }
        }
        
        @Override
        public int getItemCount() {
            return chats.size();
        }
        
        class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            TextView messageText;
            TextView timeText;
            TextView unreadCount;
            
            ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.chat_name);
                messageText = itemView.findViewById(R.id.chat_last_message);
                timeText = itemView.findViewById(R.id.chat_time);
                unreadCount = itemView.findViewById(R.id.chat_unread_count);
                
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // TODO: Open chat conversation
                    }
                });
            }
        }
    }
} 