package com.tablonca.obedimc.tghcambios;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyService extends FirebaseMessagingService {
    private static final String NUEVA_TASA = "Nueva Tasa";

    public MyService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0 && remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String ntStr = remoteMessage.getData().get(NUEVA_TASA);
        float nt = Float.valueOf(ntStr != null ? ntStr : "0");


        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(NUEVA_TASA, nt);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            Notification.Builder notificationBuilder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setColor(nt > .4 ?
                        ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary) :
                        ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = null;
                if (nt == 1) {
                    channelId = getString(R.string.peru_channel_id);
                } else if (nt == 2) {
                    channelId = getString(R.string.colombia_channel_id);
                } else if (nt == 3) {
                    channelId = getString(R.string.ecuador_channel_id);
                }
                String channelName = null;
                if (nt == 1) {
                    channelName = getString(R.string.peru_channel_name);
                } else if (nt == 2) {
                    channelName = getString(R.string.colombia_channel_name);
                } else if (nt == 3) {
                    channelName = getString(R.string.ecuador_channel_name);
                }
                NotificationChannel channel = new NotificationChannel(channelId, channelName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 200, 50});
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }

                notificationBuilder.setChannelId(channelId);
            }

            if (notificationManager != null) {
                notificationManager.notify("", 0, notificationBuilder.build());
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        Log.d("new Token", token);
    }
}
