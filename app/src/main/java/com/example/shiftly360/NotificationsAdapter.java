package com.example.shiftly360;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<NotificationItem> notifications;

    public NotificationsAdapter(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);
        holder.titleView.setText(notification.getTitle());
        holder.messageView.setText(notification.getMessage());
        holder.timeView.setText(DateUtils.getRelativeTimeSpanString(
            notification.getTimestamp(),
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView messageView;
        TextView timeView;

        ViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.notification_title);
            messageView = itemView.findViewById(R.id.notification_message);
            timeView = itemView.findViewById(R.id.notification_time);
        }
    }
} 