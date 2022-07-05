package com.aftab.clock.Services;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String body = remoteMessage.getNotification().getBody();
        String title = remoteMessage.getNotification().getTitle();

    }


}
