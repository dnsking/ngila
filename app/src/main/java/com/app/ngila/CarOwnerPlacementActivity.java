package com.app.ngila;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.app.ngila.data.AvailableCars;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.locationhandler.SingleShotLocationProvider;
import com.app.ngila.network.NetworkContentHelper;
import com.app.ngila.network.actions.AvailableCarsNetworkAction;
import com.app.ngila.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CarOwnerPlacementActivity extends AppCompatActivity {

    private NgilaUser ngilaUser;
    private TextInputLayout operatingAreaTextInputEditTextHolder;
    private TextInputEditText operatingAreaTextInputEditText;
    private LottieAnimationView loadingView;
    private Button startTime,endTime,placeBtn;
    private String pickupTime,returnTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       ngilaUser = Utils.GetNgilaUser(this);

        setContentView(R.layout.activity_car_owner_placement);

        operatingAreaTextInputEditTextHolder =findViewById(R.id.operatingAreaTextInputEditTextHolder);
        operatingAreaTextInputEditText =findViewById(R.id.operatingAreaTextInputEditText);
        loadingView =findViewById(R.id.loadingView);
        startTime =findViewById(R.id.startTime);
        endTime =findViewById(R.id.endTime);
        placeBtn = findViewById(R.id.placeBtn);

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar currentTime = Calendar.getInstance();
                MaterialTimePicker materialTimePicker = new MaterialTimePicker();
                materialTimePicker.setTimeFormat(TimeFormat.CLOCK_24H);
                materialTimePicker.setHour(currentTime.get(Calendar.HOUR_OF_DAY)+1);
                materialTimePicker.setListener(new MaterialTimePicker.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(MaterialTimePicker dialog) {
                        pickupTime=  dialog.getHour()+":"+(dialog.getMinute()==0?"00":dialog.getMinute());
                        startTime.setText(pickupTime);

                    }
                });

                materialTimePicker.show(getSupportFragmentManager(), "startTime");

            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar currentTime = Calendar.getInstance();
                MaterialTimePicker materialTimePicker = new MaterialTimePicker();
                materialTimePicker.setTimeFormat(TimeFormat.CLOCK_24H);
                materialTimePicker.setHour(currentTime.get(Calendar.HOUR_OF_DAY)+5);
                materialTimePicker.setListener(new MaterialTimePicker.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(MaterialTimePicker dialog) {
                        returnTime=  dialog.getHour()+":"+(dialog.getMinute()==0?"00":dialog.getMinute());
                        endTime.setText(returnTime);

                    }
                });

                materialTimePicker.show(getSupportFragmentManager(), "startTime");

            }
        });
        placeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAdd();
            }
        });
    }
    private void checkAdd(){
        if(pickupTime!=null&&returnTime!=null){
            startTime.setEnabled(false);
            endTime.setEnabled(false);
            placeBtn.setEnabled(false);
            operatingAreaTextInputEditTextHolder.setEnabled(false);
            loadingView.setVisibility(View.VISIBLE);
            Nammu.askForPermission(this, Build.VERSION.SDK_INT>28? new String[]{ACCESS_BACKGROUND_LOCATION,
                            ACCESS_FINE_LOCATION}:new String[]{
                            ACCESS_FINE_LOCATION}
                    , new PermissionCallback() {
                        @Override
                        public void permissionGranted() {

                            addAvailableCar();
                        }

                        @Override
                        public void permissionRefused() {

                            startTime.setEnabled(true);
                            endTime.setEnabled(true);
                            placeBtn.setEnabled(true);
                            operatingAreaTextInputEditTextHolder.setEnabled(true);
                            loadingView.setVisibility(View.GONE);
                            Snackbar.make(placeBtn,"Location Permission Required®",Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
        else if(pickupTime==null){
            Snackbar.make(placeBtn,"Set Pick Up Time",Snackbar.LENGTH_SHORT).show();
        }
        else if(returnTime==null){
            Snackbar.make(placeBtn,"Set Return Time",Snackbar.LENGTH_SHORT).show();
        }
    }
    private void addAvailableCar(){
        SingleShotLocationProvider.requestSingleUpdate(CarOwnerPlacementActivity.this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {

                        LatLng latLng = new LatLng(location.latitude,location.longitude);

                        try {
                            AvailableCars availableCars = new AvailableCars();
                            availableCars.setCarModel(ngilaUser.getCarModel());
                            availableCars.setLicensePlate(ngilaUser.getNumberPlate());
                            availableCars.setPhoneNumber(ngilaUser.getPhoneNumber());
                            availableCars.setCity(Utils.LocationCity(CarOwnerPlacementActivity.this,
                                    location.latitude,location.longitude ));
                            availableCars.setLocation(Utils.LatLonOBjToString(latLng));
                            availableCars.setOperatingArea(
                                    operatingAreaTextInputEditText.getText().toString().length()>0
                                    ?operatingAreaTextInputEditText.getText().toString():""
                            );
                            availableCars.setLocationName(Utils.AdressName(
                                    CarOwnerPlacementActivity.this,
                                    location.latitude,location.longitude
                            ));
                            availableCars.setPickUpTime(pickupTime);
                            availableCars.setReturnTime(returnTime);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {


                                    AvailableCarsNetworkAction availableCarsNetworkAction = new AvailableCarsNetworkAction();
                                    availableCarsNetworkAction.setData(availableCars);
                                    try {
                                        NetworkContentHelper.AddContent(CarOwnerPlacementActivity.this,
                                                availableCarsNetworkAction);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent =    new Intent();
                                                intent.putExtra(App.Content,new Gson().toJson(availableCarsNetworkAction));
                                                setResult(100,intent);
                                               finish();
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}