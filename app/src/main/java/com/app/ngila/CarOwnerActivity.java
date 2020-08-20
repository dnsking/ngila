package com.app.ngila;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.app.ngila.data.DriverStatus;
import com.app.ngila.data.DriverTravel;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.locationhandler.SingleShotLocationProvider;
import com.app.ngila.network.NetworkContentHelper;
import com.app.ngila.network.actions.SignInNetworkAction;
import com.app.ngila.utils.Utils;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Response;

public class CarOwnerActivity extends AppCompatActivity {

    private NgilaUser ngilaUser;
    private RecyclerView timeline;
    private FrameLayout asker;
    private final int requestCode =100;
    private String bookedDriver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookedDriver = Utils.GetString(App.BookedDriver,this);
        ngilaUser = Utils.GetNgilaUser(this);
        setContentView(R.layout.activity_car_owner);
        timeline = findViewById(R.id.timeline);
        asker = findViewById(R.id.asker);
        init();

    }
    private void showNewBooking(){
        asker.setVisibility(View.VISIBLE);
        findViewById(R.id.placeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarOwnerActivity.this,CarOwnerPlacementActivity.class);
                startActivityForResult(intent,requestCode);
            }
        });
    }
    private void showTimeLine(){}
    public void init(){
        if(bookedDriver==null){
            showNewBooking();
        }
        else{}
     /*   new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    SignInNetworkAction getPassengerBookedNetworkAction=  new SignInNetworkAction(ngilaUser.getPhoneNumber()
                            ,"GetCarownerStatus");


                    Response response = NetworkContentHelper.ApiGatewayCaller(getPassengerBookedNetworkAction);

                    String result = response.body().string();
                    App. Log("DriverTravel "+result);

                    DriverStatus driverStatus = new Gson().fromJson(result,DriverStatus.class);
                    if(driverStatus.getAvailable()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showNewBooking();
                            }
                        });
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showTimeLine();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }
}