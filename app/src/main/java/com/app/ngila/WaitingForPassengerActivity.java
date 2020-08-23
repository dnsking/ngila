package com.app.ngila;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.app.ngila.data.DriverOrder;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.locationhandler.SingleShotLocationProvider;
import com.app.ngila.network.NetworkContentHelper;
import com.app.ngila.network.actions.AddPassengerPickUp;
import com.app.ngila.network.actions.DriverCarOwnerContract;
import com.app.ngila.network.actions.DriverWaiting;
import com.app.ngila.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class WaitingForPassengerActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private SupportMapFragment mapFragment ;
    private GoogleMap mMap;
    private NgilaUser ngilaUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ngilaUser = Utils.GetNgilaUser(this);
        setContentView(R.layout.activity_waiting_for_passenger);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);



        Nammu.askForPermission(this, Build.VERSION.SDK_INT>28? new String[]{ACCESS_BACKGROUND_LOCATION,
                        ACCESS_FINE_LOCATION}:new String[]{
                        ACCESS_FINE_LOCATION}
                , new PermissionCallback() {
                    @Override
                    public void permissionGranted() {

                        mapFragment.getMapAsync(WaitingForPassengerActivity.this);
                        init();
                    }

                    @Override
                    public void permissionRefused() {

                        //   Snackbar.make(placeBtn,"Location Permission Required®",Snackbar.LENGTH_SHORT).show();
                    }
                });

        registerReceiver(new OrderReceiver(),
                new IntentFilter(App.Order));
    }
    private class OrderReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String json = intent.getStringExtra(App.Content);
            DriverOrder oder =new Gson().fromJson(json,DriverOrder.class);

        }
    }
    private void init(){

        SingleShotLocationProvider.requestSingleUpdate(WaitingForPassengerActivity.this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {

                        try{
                            String   city = Utils.LocationCity(WaitingForPassengerActivity.this, location.latitude, location.longitude);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DriverWaiting driverWaiting = new DriverWaiting(city,ngilaUser.getPhoneNumber());

                                    try {
                                        NetworkContentHelper.AddContent(WaitingForPassengerActivity.this,driverWaiting);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();
                        }
                        catch (Exception ex){}
                    }
                });


    }
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);


        SingleShotLocationProvider.requestSingleUpdate(this, new SingleShotLocationProvider.LocationCallback() {
            @Override
            public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {


                // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 24));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.latitude, location.longitude))      // Sets the center of the map to location user
                        .zoom(30)                   // Sets the zoom
                        .bearing(30)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}