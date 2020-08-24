package com.app.ngila;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.ngila.data.AvailableCars;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.locationhandler.SingleShotLocationProvider;
import com.app.ngila.network.NetworkContentHelper;
import com.app.ngila.network.actions.AvailableCarsNetworkAction;
import com.app.ngila.network.actions.DriverCarOwnerContract;
import com.app.ngila.utils.Utils;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Calendar;

public class CarOwnerScanActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private NgilaUser ngilaUser;
    private String location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = getIntent().getStringExtra(App.Content);
        ngilaUser = Utils.GetNgilaUser(this);
        setContentView(R.layout.activity_car_owner_scan);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SingleShotLocationProvider.requestSingleUpdate(CarOwnerScanActivity.this,
                                new SingleShotLocationProvider.LocationCallback() {
                                    @Override
                                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {

                                        LatLng latLng = new LatLng(location.latitude,location.longitude);
                                        String locationStr = Utils.LatLonOBjToString(latLng);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try{
                                                    NgilaUser ngilaDriverUser= new Gson().fromJson(result.getText(),NgilaUser.class);
                                                    Calendar c = Calendar.getInstance();
                                                    DriverCarOwnerContract driverCarOwnerContract = new
                                                            DriverCarOwnerContract(
                                                            Long.toString(c.getTimeInMillis()),ngilaUser.getPhoneNumber(),
                                                            ngilaDriverUser.getPhoneNumber(),locationStr
                                                            ,App.DriverContractStarted);
                                                    NetworkContentHelper.AddContent(CarOwnerScanActivity.this,driverCarOwnerContract);}
                                                catch (Exception ex){

                                                }
                                                finish();
                                            }
                                        }).start();


                                    }
                                });
                    }
                });



            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}