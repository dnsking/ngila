package com.app.ngila;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.app.ngila.locationhandler.SingleShotLocationProvider;
import com.app.ngila.network.actions.AcceptedDriver;
import com.app.ngila.network.actions.DriverCarOwnerContract;
import com.app.ngila.utils.Utils;
import com.app.ngila.views.TimelineViewHelper;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import java.io.IOException;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class CarBookedActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private MaterialToolbar toolbar;
    private  AcceptedDriver acceptedDriver;
    private DriverCarOwnerContract driverCarOwnerContract;

    private SupportMapFragment mapFragment ;
    private GoogleMap mMap;
    private Marker marker;
    private RecyclerView timeline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        acceptedDriver =
                new Gson().fromJson(Utils.GetString(App.BookedDriverAcceptedData,this),AcceptedDriver.class);
        driverCarOwnerContract =
                new Gson().fromJson(Utils.GetString(App.BookedDriverAcceptedHiredData,this),DriverCarOwnerContract.class);

        setContentView(R.layout.activity_car_booked);

        timeline = findViewById(R.id.timeline);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(acceptedDriver.getName());



        TimelineViewHelper.InitList(timeline,new TimelineViewHelper.TimeLineItem[]{
                new TimelineViewHelper.TimeLineItem("Contract Started"
                        , TimeAgo.using(Long.parseLong( acceptedDriver.getTime())),0)
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        Nammu.askForPermission(this, Build.VERSION.SDK_INT>28? new String[]{ACCESS_BACKGROUND_LOCATION,
                        ACCESS_FINE_LOCATION}:new String[]{
                        ACCESS_FINE_LOCATION}
                , new PermissionCallback() {
                    @Override
                    public void permissionGranted() {



                        mapFragment.getMapAsync(CarBookedActivity.this);
                    }

                    @Override
                    public void permissionRefused() {

                        //   Snackbar.make(placeBtn,"Location Permission Required®",Snackbar.LENGTH_SHORT).show();
                    }
                });


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
        mMap.setMyLocationEnabled(false);

        if(marker!=null)
            marker.remove();

        LatLng location = Utils.LatLonOBjFromString(driverCarOwnerContract.getDriverLocation());
        marker=  mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                .title("Car Location").icon(Utils.CarIconSmall(this)));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.latitude, location.longitude))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(30)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
}