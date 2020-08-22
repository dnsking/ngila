package com.app.ngila;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.ngila.data.AvailableCars;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.locationhandler.SingleShotLocationProvider;
import com.app.ngila.network.NetworkContentHelper;
import com.app.ngila.network.actions.AcceptedDriver;
import com.app.ngila.network.actions.AvailableCarsNetworkAction;
import com.app.ngila.network.actions.DriverCarOwnerContract;
import com.app.ngila.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.Calendar;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class DriverCodeActivity extends AppCompatActivity {

    private NgilaUser ngilaUser;
    private ImageView qrImageView;
    private AvailableCars availableCars;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        availableCars = new Gson().fromJson(getIntent().getStringExtra(App.Content),AvailableCars.class);

        ngilaUser = Utils.GetNgilaUser(this);
        ngilaUser.setPassword(null);
        setContentView(R.layout.activity_driver_code);

        qrImageView = findViewById(R.id.qrImageView);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        QRGEncoder qrgEncoder = new QRGEncoder(new Gson().toJson(ngilaUser),  QRGContents.Type.TEXT,smallerDimension);
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);
        Calendar calendar = Calendar.getInstance();

        qrImageView.setImageBitmap(qrgEncoder.getBitmap());

        SingleShotLocationProvider.requestSingleUpdate(DriverCodeActivity.this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {

                        LatLng latLng = new LatLng(location.latitude,location.longitude);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    AcceptedDriver acceptedDriver = new AcceptedDriver(
                                            ngilaUser.getPhoneNumber(),availableCars.getPhoneNumber(),
                                            ngilaUser.getFirstName()+" "+ngilaUser.getLastName()
                                            ,Long.toString(calendar.getTimeInMillis()),
                                            Utils.LatLonOBjToString(latLng),availableCars.getLocation());
                                    try {
                                        NetworkContentHelper.AddContent(DriverCodeActivity.this,
                                                acceptedDriver);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();


                    }
                });

        registerReceiver(new DriverCarOwnerContractReceiver(),
                new IntentFilter(App.DriverCarOwnerContractBroadcast));
    }
    private class DriverCarOwnerContractReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra(App.Content);
            Utils.SaveString(App.BookedDriver,context,App.BookedDriverAcceptedHired);
            DriverCarOwnerContract acceptedDriver = new Gson().fromJson(json, DriverCarOwnerContract.class);
            Utils.SaveString(App.BookedDriverAcceptedHiredData,context,json);

        }
    }
}