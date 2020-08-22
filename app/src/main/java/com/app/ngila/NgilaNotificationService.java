package com.app.ngila;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.app.ngila.network.actions.AcceptedDriver;
import com.app.ngila.network.actions.DriverCarOwnerContract;
import com.app.ngila.network.actions.NetworkAction;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.concurrent.ThreadLocalRandom;

public class NgilaNotificationService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

//      Utils. SendDeviceToServer(AlbatalNotificationService.this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        App.Log("onMessageReceived "+remoteMessage.getFrom());

        App.Log("onMessageReceived "+remoteMessage.getData());
        App.Log("onMessageReceived "+remoteMessage.getData().get("default"));


        String json =remoteMessage.getData().get("default");
    //    NetworkAction networkAction =  new Gson().fromJson(json, NetworkAction.class);
        assert json != null;
        if(json.contains("carOwnerLocation")){

            Intent intent = new Intent(App.AcceptedDriverBroadcast);
            intent.putExtra(App.Content,json);
            sendBroadcast(intent);
        }

        else if(json.contains("driverActivity")){

            Intent intent = new Intent(App.DriverCarOwnerContractBroadcast);
            intent.putExtra(App.Content,json);
            sendBroadcast(intent);
        }
        /*
        if(json.contains("from")&&json.contains("messageId")&&json.contains("messageId")){

            AlbatalMessage msg = new Gson().fromJson(json, AlbatalMessage.class);
            MessagesDBHelper   mMessagesDBHelper = new MessagesDBHelper(this);
            mMessagesDBHelper.open();

            mMessagesDBHelper.insertEntry(msg);
            mMessagesDBHelper.close();
            Intent intent = new Intent(App.NewMsg);
            intent.putExtra(App.Content,json);
            sendBroadcast(intent);
            App.Log("send sgm");
        }
        else{
            Intent notificationIntent = new Intent(this, PropertyViewingActivityActivity.class);
            notificationIntent.putExtra(App.Content,json);
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Property mProperty =new Gson().fromJson(json,Property.class);
            String channelId = "channel-idU";
            String channelName = "Channel U";

            final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            final int NOTIFICATION_REF =  ThreadLocalRandom.current().nextInt(1, 10000 + 1);
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channelId);

            builder.setTicker(
                    mProperty.getPromotionalText()==null?"Listing Property"
                            :"Promotional Text").setContentTitle(
                    mProperty.getPromotionalText()==null?"Listing Property"
                            :"Promotion")
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setContentText(
                            mProperty.getPromotionalText()==null?"New Listing Property"
                                    :mProperty.getPromotionalText())
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_albatal).setLargeIcon(
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_large_icon_foreground));

            Glide.with(this)
                    .asBitmap()
                    .load(App.BucketUrl+mProperty.getImages()[0])
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            builder.setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(resource));


                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                int importance = NotificationManager.IMPORTANCE_LOW;
                                NotificationChannel mChannel = new NotificationChannel(
                                        channelId, channelName, importance);
                                notificationManager.createNotificationChannel(mChannel);
                            }
                            Notification notification = builder.build();

                            notificationManager.notify(NOTIFICATION_REF, notification);
                        }
                    });





        }*/
      /*  if (remoteMessage.getData().size() > 0) {
            String data =remoteMessage.getData().get("default");

            if(data!=null){

                Intent intent = new Intent(App.NewUser);
                intent.putExtra(App.Content,data);
                sendBroadcast(intent);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        TringContact tringContact= new Gson().fromJson(data,TringContact.class);

                        try {
                            Utils.SaveFileToCache( TringNotificationService.this, TringUploadHelper.FetchDownloadUrl(TringNotificationService.this,
                                    tringContact.getPhoneNumber().replaceAll("\\+","")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();


            }
        }*/


    }
}
