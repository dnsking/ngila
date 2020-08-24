package com.app.ngila;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.ngila.data.DriverStatus;
import com.app.ngila.data.DriverTravel;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.locationhandler.SingleShotLocationProvider;
import com.app.ngila.network.NetworkContentHelper;
import com.app.ngila.network.actions.AcceptedDriver;
import com.app.ngila.network.actions.DriverCarOwnerContract;
import com.app.ngila.network.actions.SignInNetworkAction;
import com.app.ngila.utils.Utils;
import com.app.ngila.views.TimelineViewHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class CarOwnerActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private NgilaUser ngilaUser;
    private RecyclerView timeline;
    private FrameLayout asker;
    private final int requestCode =100;
    private String bookedDriver;
    private View connectionAnimation;
    private View scanCodeBtn;


    private SupportMapFragment mapFragment ;
    private GoogleMap mMap;
    private LatLng currentLocation;
    private LatLng destinationLocation;
    private Marker marker;
    private TextView locationTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookedDriver = Utils.GetString(App.BookedDriver,this);
        ngilaUser = Utils.GetNgilaUser(this);
        setContentView(R.layout.activity_car_owner);
        scanCodeBtn = findViewById(R.id.scanCodeBtn);
        connectionAnimation = findViewById(R.id.connectionAnimation);
        timeline = findViewById(R.id.timeline);
        asker = findViewById(R.id.asker);

        locationTextView = findViewById(R.id.locationTextView);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);



        Nammu.askForPermission(this, Build.VERSION.SDK_INT>28? new String[]{ACCESS_BACKGROUND_LOCATION,
                        ACCESS_FINE_LOCATION}:new String[]{
                        ACCESS_FINE_LOCATION}
                , new PermissionCallback() {
                    @Override
                    public void permissionGranted() {



                        mapFragment.getMapAsync(CarOwnerActivity.this);
                        init();
                    }

                    @Override
                    public void permissionRefused() {

                        //   Snackbar.make(placeBtn,"Location Permission Required®",Snackbar.LENGTH_SHORT).show();
                    }
                });
        //init();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {

                currentLocation =arg0;
                if(marker!=null)
                    marker.remove();
                marker= mMap.addMarker(new MarkerOptions().position(arg0)
                        .title("Car Location"));
                try {
                    locationTextView.setText(Utils.AdressName(CarOwnerActivity.this,arg0.latitude,arg0.longitude));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(arg0, 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(arg0)      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(30)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });

        SingleShotLocationProvider.requestSingleUpdate(this, new SingleShotLocationProvider.LocationCallback() {
            @Override
            public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {

                currentLocation =new LatLng(location.latitude, location.longitude);
                marker=  mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                        .title("Pick Up"));
                try {
                    locationTextView.setText(Utils.AdressName(CarOwnerActivity.this,location.latitude,location.longitude));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.latitude, location.longitude))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(30)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getStringExtra(App.Content)!=null){

            Utils.SaveString(App.BookedDriver,this,App.BookedDriverWaiting);
            showTransaction();
        }
    }
    private class BookedDriverWaitingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra(App.Content);
            Utils.SaveString(App.BookedDriverAcceptedData,context,json);
            Utils.SaveString(App.BookedDriver,context,App.BookedDriverAccepted);
            AcceptedDriver acceptedDriver = new Gson().fromJson(json, AcceptedDriver.class);
            showTransaction();
        }
    }

    private class DriverCarOwnerContractReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra(App.Content);
            Utils.SaveString(App.BookedDriver,context,App.BookedDriverAcceptedHired);
            DriverCarOwnerContract acceptedDriver = new Gson().fromJson(json, DriverCarOwnerContract.class);
            Utils.SaveString(App.BookedDriverAcceptedHiredData,context,json);
            showTransaction();
        }
    }
    private void showTransaction(){

        scanCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Nammu.askForPermission(CarOwnerActivity.this, CAMERA
                        , new PermissionCallback() {
                            @Override
                            public void permissionGranted() {


                                Intent intent = new Intent( CarOwnerActivity.this, CarOwnerScanActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void permissionRefused() {

                            }
                        });
            }
        });
        bookedDriver = Utils.GetString(App.BookedDriver,this);
        timeline.setVisibility(View.VISIBLE);
        asker.setVisibility(View.GONE);

        findViewById(R.id.connectionHolder).setVisibility(View.GONE);


        registerReceiver(new BookedDriverWaitingReceiver(),
                new IntentFilter(App.AcceptedDriverBroadcast));
        registerReceiver(new DriverCarOwnerContractReceiver(),
                new IntentFilter(App.DriverCarOwnerContractBroadcast));


        if(bookedDriver.equals(App.BookedDriverWaiting)){



            TimelineViewHelper.InitList(timeline,new TimelineViewHelper.TimeLineItem[]{
                    new TimelineViewHelper.TimeLineItem("Request Sent"
                            ,"A Driver Will Soon Get To You",0)
            });

        }
        else if(bookedDriver.equals(App.BookedDriverAccepted)){
            findViewById(R.id.connectionHolder).setVisibility(View.VISIBLE);

            AcceptedDriver acceptedDriver =
                    new Gson().fromJson(Utils.GetString(App.BookedDriverAcceptedData,this),AcceptedDriver.class);


            double distance = SphericalUtil.computeDistanceBetween(Utils.LatLonOBjFromString(acceptedDriver.getDriverLocation())
                    , Utils.LatLonOBjFromString(acceptedDriver.getCarOwnerLocation()));

            TimelineViewHelper.InitList(timeline,new TimelineViewHelper.TimeLineItem[]{
                    new TimelineViewHelper.TimeLineItem("Driver "+acceptedDriver.getName()+" on their way"
                            ,Utils.RoundOff(distance/1000.00)+" km away",0)
            });

        }
        else if(bookedDriver.equals(App.BookedDriverAcceptedHired)){

           final DriverCarOwnerContract acceptedDriver =
                    new Gson().fromJson(Utils.GetString(App.BookedDriverAcceptedHiredData,this),DriverCarOwnerContract.class);

           Intent intent = new Intent(this,CarBookedActivity.class);
           startActivity(intent);
           finish();
       /*     new Thread(new Runnable() {
                @Override
                public void run() {
                    try{

                        SignInNetworkAction getPassengerBookedNetworkAction=  new SignInNetworkAction(
                                acceptedDriver.getCarOwnerNumber()
                                ,"GetContract");

                        Response response = NetworkContentHelper.ApiGatewayCaller(getPassengerBookedNetworkAction);

                        String result = response.body().string();
                        App. Log("BookedDriverAcceptedHired "+result);
                        DriverCarOwnerContract[] driverCarOwnerContracts = new Gson().fromJson(result,DriverCarOwnerContract[].class);
                        ArrayList<TimelineViewHelper.TimeLineItem> items = new ArrayList<>();
                        for(DriverCarOwnerContract singledriverCarOwnerContract : driverCarOwnerContracts){
                            LatLng location = Utils.LatLonOBjFromString(singledriverCarOwnerContract.getDriverLocation());
                            items.add(
                                    new TimelineViewHelper.TimeLineItem(singledriverCarOwnerContract.getDriverActivity()
                                            ,
                                            Utils.AdressName(CarOwnerActivity.this,location.latitude,location.longitude
                                                    ),0));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                TimelineViewHelper.InitList(timeline,items.toArray( new TimelineViewHelper.TimeLineItem[]{}));
                            }
                        });
                    }
                    catch (Exception ex){

                    }
                }
            }).start();
      */  }

    }
    private void showNewBooking(){

        SingleShotLocationProvider.requestSingleUpdate(this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        currentLocation = new LatLng(location.latitude,location.longitude);
                    }
                });


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
        else{
            showTransaction();
        }
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