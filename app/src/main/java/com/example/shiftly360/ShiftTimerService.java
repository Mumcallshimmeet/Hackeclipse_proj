package com.example.shiftly360;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class ShiftTimerService extends Service {
    private static final String TAG = "ShiftTimerService";
    private final IBinder binder = new LocalBinder();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ShiftUpdateListener listener;
    private boolean isRunning = false;
    private long startTime = 0;
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "ShiftTimer";
    private static final int UPDATE_INTERVAL = 60000; // 1 minute

    public class LocalBinder extends Binder {
        ShiftTimerService getService() {
            return ShiftTimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        createNotificationChannel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service onBind");
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service onDestroy");
        super.onDestroy();
        if (isRunning) {
            stopShift();
        }
    }

    public void startShift() {
        try {
            Log.d(TAG, "Starting shift");
            if (!isRunning) {
                isRunning = true;
                startTime = System.currentTimeMillis();
                Notification notification = createNotification();
                if (notification != null) {
                    startForeground(NOTIFICATION_ID, notification);
                    updateTimer();
                    Log.d(TAG, "Shift started successfully");
                } else {
                    Log.e(TAG, "Failed to create notification");
                    isRunning = false;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting shift: " + e.getMessage());
            isRunning = false;
        }
    }

    public void stopShift() {
        try {
            Log.d(TAG, "Stopping shift");
            isRunning = false;
            handler.removeCallbacksAndMessages(null);
            stopForeground(true);
            Log.d(TAG, "Shift stopped successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping shift: " + e.getMessage());
        }
    }

    private void updateTimer() {
        if (!isRunning) return;
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    if (listener != null) {
                        long currentDuration = System.currentTimeMillis() - startTime;
                        listener.onShiftTimeUpdate(startTime, currentDuration);
                    }
                    updateNotification();
                    updateTimer();
                }
            }
        }, UPDATE_INTERVAL);
    }

    public void setListener(ShiftUpdateListener listener) {
        this.listener = listener;
        Log.d(TAG, "Listener set: " + (listener != null));
    }

    private void createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Shift Timer",
                    NotificationManager.IMPORTANCE_LOW
                );
                channel.setDescription("Shows ongoing shift information");
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                    Log.d(TAG, "Notification channel created");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating notification channel: " + e.getMessage());
        }
    }

    private Notification createNotification() {
        try {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Shift in Progress")
                .setContentText("Tracking your shift time")
                .setSmallIcon(R.drawable.ic_shifts)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

            return builder.build();
        } catch (Exception e) {
            Log.e(TAG, "Error creating notification: " + e.getMessage());
            return null;
        }
    }

    private void updateNotification() {
        try {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null && isRunning) {
                Notification notification = createNotification();
                if (notification != null) {
                    manager.notify(NOTIFICATION_ID, notification);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating notification: " + e.getMessage());
        }
    }

    public interface ShiftUpdateListener {
        void onShiftTimeUpdate(long startTime, long duration);
    }
} 