package com.panda.stuedent_map_1;

import android.app.Service;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.v("NEW_TOKEN", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
