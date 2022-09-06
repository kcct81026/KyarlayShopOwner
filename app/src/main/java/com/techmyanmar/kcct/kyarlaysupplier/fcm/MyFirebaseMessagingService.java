package com.techmyanmar.kcct.kyarlaysupplier.fcm;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techmyanmar.kcct.kyarlaysupplier.R;
import com.techmyanmar.kcct.kyarlaysupplier.activity.NotificationActivity;
import com.techmyanmar.kcct.kyarlaysupplier.operation.AppController;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ConstanceVariable;
import com.techmyanmar.kcct.kyarlaysupplier.operation.Constant;
import com.techmyanmar.kcct.kyarlaysupplier.operation.LocaleHelper;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService implements ConstanceVariable, Constant {

    private static final String TAG = "MyFirebaseMessagingServ";

    Context context;
    Bitmap bitmap;
    Resources resources;

    SharedPreferences prefs;
    String refreshedToken;
    String url, type, notiTitle, notiBody, notiUser, youtubeID;
    String notiUrl = "";

    NotificationChannel mChannel = null;
    NotificationManager notifManager = null;
    int importance = NotificationManager.IMPORTANCE_HIGH;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(0);

        prefs = getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        try{
            Log.e(TAG, "onMessageReceived: ----------------- "  + remoteMessage.getData() );
            url  = remoteMessage.getData().get("url");
            type = remoteMessage.getData().get("type");
            notiUrl   = remoteMessage.getData().get("image_url");
            notiTitle = remoteMessage.getData().get("title");
            notiBody  = remoteMessage.getData().get("body");


/*
            if (type.equals("comment" )) {
                notiUser = remoteMessage.getData().get("user_id");
            }
            if(type.equals("video")){
                youtubeID = remoteMessage.getData().get("youtube_id");
            }

            if (notifManager == null) {
                notifManager = (NotificationManager) getSystemService
                        (Context.NOTIFICATION_SERVICE);
            }

            if(notiUrl.trim().length() > 0){
                bitmap = getBitmapfromUrl(notiUrl);
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationOreo();
            }else{
                notificationUnderOreo();
            }*/

        }catch(Exception e){
            Log.e(TAG, "Exception : "  +e.getMessage());
        }


    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        // Sending new token to AppsFlyer

        prefs =  getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        FirebaseMessaging.getInstance().subscribeToTopic("regular");
        refreshedToken      = FirebaseInstanceId.getInstance().getToken();


        if (refreshedToken != null && !refreshedToken.equals("")) {

            prefs.edit().putString(SP_FCM_TOEKN, refreshedToken).commit();
            //updateFCMID();

        }
    }


    public void notificationUnderOreo() {

        if (type.equals("main")) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            Intent intent = new Intent(this, NotificationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("fromClass", FROM_FIREBASE);
            intent.putExtras(bundle);

            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //builder.setSmallIcon(R.drawable.menu_kyarlay);
            builder.setContentTitle(notiTitle);
            builder.setContentText(notiBody);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setAutoCancel(true);
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            builder.setOnlyAlertOnce(true);
            builder.setVibrate(new long[]{50, 125});
            builder.setContentIntent(pendingIntent);
            Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_icon);
            builder.setColor(getResources().getColor(R.color.background)).setSmallIcon(R.mipmap.ic_icon).setLargeIcon(bitmap);

            if (bitmap != null) {
                NotificationCompat.BigPictureStyle bigPicStyle = new NotificationCompat.BigPictureStyle();
                bigPicStyle.bigPicture(bitmap);
                bigPicStyle.setBigContentTitle(notiTitle);
                bigPicStyle.setSummaryText(notiBody);
                builder.setStyle(bigPicStyle);
            }
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //notificationManager.cancel(NOTIFICATION_ID);
            notificationManager.notify(0, builder.build());
        }

    }


    private void updateFCMID(){
        JSONObject updateFcm = new JSONObject();
        try {
            updateFcm.put("fcm_token",  refreshedToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,  String.format(constantUpdateFcmID, prefs.getInt(SP_ID, 0)), updateFcm,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Customer-Phone", prefs.getString(SP_PHONE,""));
                headers.put("X-Customer-Token", prefs.getString(SP_TOKEN,""));
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq,"sign_in");
    }


    @SuppressLint("WrongConstant")
    public void notificationOreo() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            if (type.equals("main")) {
                final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "4");

                Intent intent = new Intent(this, NotificationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("fromClass", FROM_FIREBASE);
                intent.putExtras(bundle);


                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                //builder.setSmallIcon(R.drawable.menu_kyarlay);
                builder.setContentTitle(notiTitle);
                builder.setContentText(notiBody);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setAutoCancel(true);
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                builder.setOnlyAlertOnce(true);
                builder.setVibrate(new long[]{50, 125});
                builder.setContentIntent(pendingIntent);
                Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_icon);

                builder.setColor(getResources().getColor(R.color.background)).setSmallIcon(R.mipmap.ic_icon).setLargeIcon(bitmap);

                if (mChannel == null) {
                    mChannel = new NotificationChannel
                            ("4", notiTitle, importance);
                    mChannel.setDescription(notiTitle);
                    mChannel.enableVibration(true);
                    notifManager.createNotificationChannel(mChannel);
                }

                if (bitmap != null) {
                    NotificationCompat.BigPictureStyle bigPicStyle = new NotificationCompat.BigPictureStyle();
                    bigPicStyle.bigPicture(bitmap);
                    bigPicStyle.setBigContentTitle(notiTitle);
                    bigPicStyle.setSummaryText(notiBody);
                    builder.setStyle(bigPicStyle);
                }
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                //notificationManager.cancel(NOTIFICATION_ID);
                notificationManager.notify(0, builder.build());

            }

        }
    }



    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "My channel ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_icon)
                        .setContentTitle("Your product status")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotification(String from, String body) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: -------------" + from + " => " + body );
                Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(), from + " => " + body, Toast.LENGTH_LONG).show();
            }
        });
    }

}
