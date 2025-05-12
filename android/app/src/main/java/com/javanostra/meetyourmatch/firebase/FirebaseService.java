package com.javanostra.meetyourmatch.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.RetrofitClient;
import com.javanostra.meetyourmatch.persistance.UserSession;
import com.javanostra.meetyourmatch.persistance.cookie.CookieManager;

public class FirebaseService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    private static final String CHANNEL_ID = "id.apps.notification";
    private static final String CHANNEL_NAME = "NotificationChannel";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null
                && UserSession.currentUser != null
                && String.valueOf(UserSession.currentUser.getId()).equals(remoteMessage.getData().get("userId"))) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Notification title: " + title);
            Log.d(TAG, "Notification body: " + body);

            showNotification(title, body);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, token);
    }

    private void showNotification(String title, String body) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mym_128_round)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
