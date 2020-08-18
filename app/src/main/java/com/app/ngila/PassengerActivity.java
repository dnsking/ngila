package com.app.ngila;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.ngila.data.DriverLocation;
import com.app.ngila.data.DriverTravel;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.locationhandler.SingleShotLocationProvider;
import com.app.ngila.network.NetworkContentHelper;
import com.app.ngila.network.actions.SignInNetworkAction;
import com.app.ngila.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;

public class PassengerActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private NgilaUser ngilaUser;
    private String city;

    private SupportMapFragment mapFragment ;
    private GoogleMap mMap;
    private LatLng currentLocation;
    private LatLng destinationLocation;
    private Marker marker;
    private ArrayList<Marker> driverMarkers;
    private TextView locationTextView;
    private int requestCode = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        locationTextView = findViewById(R.id.locationTextView);

        findViewById(R.id.actionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PassengerActivity.this,NewPassengerBookingMainActivity.class);
                intent.putExtra(App.Content,Utils.LatLonOBjToString(currentLocation));
                startActivityForResult(intent,requestCode);

            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        destinationLocation = Utils.LatLonOBjFromString(data.getStringExtra(App.Content));
     //   NgilaPassengerPickUps
    }

    private void loadCars(){

        if(driverMarkers==null)
        driverMarkers = new ArrayList<>();
        else{
            for (Marker driverMaker:
                    driverMarkers ) {
                driverMaker.remove();
            }
        }

        driverMarkers = new ArrayList<>();

        try {
            SignInNetworkAction getPassengerBookedNetworkAction=  new SignInNetworkAction(city
                    ,"GetDriverLocation");
            Response response = NetworkContentHelper.ApiGatewayCaller(getPassengerBookedNetworkAction);

            String  result = response.body().string();

            App. Log("NetworkContentHelper "+result);

            DriverLocation[] driverLocations = new Gson().fromJson(result,DriverLocation[].class);
            for(DriverLocation driverLocation:driverLocations){

                double[] location = Utils.LatLonFromString(driverLocation.getLocation());
                Marker driverMaker=  marker= mMap.addMarker(new MarkerOptions().position(new LatLng(
                        location[0],location[1]

                ))
                .icon(Utils.CarIcon(PassengerActivity.this)));
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
        new android.os.CountDownTimer(1000*3,50){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                loadCars();

            }
        }.start();
    }
    public void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SingleShotLocationProvider.requestSingleUpdate(PassengerActivity.this,
                        new SingleShotLocationProvider.LocationCallback() {
                            @Override
                            public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                                try {
                                    city = Utils.LocationCity(PassengerActivity.this,location.latitude,location.longitude);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                try {

                    SignInNetworkAction getPassengerBookedNetworkAction=  new SignInNetworkAction(ngilaUser.getPhoneNumber()
                    ,"GetPassengerBooked");


                    Response response = NetworkContentHelper.ApiGatewayCaller(getPassengerBookedNetworkAction);

                    String result = response.body().string();
                    App. Log("DriverTravel "+result);

                    DriverTravel driverTravel = new Gson().fromJson(result,DriverTravel.class);
                    if(driverTravel.getRideId()==null){
                        loadCars();

                    }
                    else{

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {

                currentLocation =arg0;
                if(marker!=null)
                    marker.remove();
                marker= mMap.addMarker(new MarkerOptions().position(arg0)
                        .title("Pick Up"));
                try {
                    locationTextView.setText(Utils.AdressName(PassengerActivity.this,arg0.latitude,arg0.longitude));
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
                    locationTextView.setText(Utils.AdressName(PassengerActivity.this,location.latitude,location.longitude));
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
}