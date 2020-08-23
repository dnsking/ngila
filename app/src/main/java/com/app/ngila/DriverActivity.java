package com.app.ngila;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.ngila.data.AvailableCars;
import com.app.ngila.data.DriverLocation;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Response;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DriverActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private NgilaUser ngilaUser;
    private String status;
    private LatLng mylocation;
    private ArrayList<AvailableCars> availableCarsList;
    private RecyclerView availableCarsRecyclerView;
    private final int requestCode=100;
    private SupportMapFragment mapFragment ;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ngilaUser = Utils.GetNgilaUser(this);
        status = Utils.GetString(App.Status,this);
        setContentView(R.layout.activity_driver2);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        availableCarsRecyclerView = findViewById(R.id.availableCarsRecyclerView);
        //if(status==null)

            Nammu.askForPermission(this, Build.VERSION.SDK_INT>28? new String[]{ACCESS_BACKGROUND_LOCATION,
                            ACCESS_FINE_LOCATION}:new String[]{
                            ACCESS_FINE_LOCATION}
                    , new PermissionCallback() {
                        @Override
                        public void permissionGranted() {

                            mapFragment.getMapAsync(DriverActivity.this);
                            showAvailableCars();
                        }

                        @Override
                        public void permissionRefused() {

                         //   Snackbar.make(placeBtn,"Location Permission Required®",Snackbar.LENGTH_SHORT).show();
                        }
                    });


        String availableCars = Utils.GetString(App.CarRequestData,this);
        String BookedDriverAcceptedHiredDataString = Utils.GetString(App.BookedDriverAcceptedHiredData,this);
        if(availableCars!=null){
        Intent intent = new Intent(DriverActivity.this,DriverCodeActivity.class);
        intent.putExtra(App.Content,availableCars);
        startActivity(intent);
        finish();}
        else if(BookedDriverAcceptedHiredDataString!=null){

            Intent intent = new Intent(DriverActivity.this,WaitingForPassengerActivity.class);
            startActivity(intent);
            finish();

        }


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
    private void showAvailableCars(){
        //Utils.SendDeviceToServer(this);

        availableCarsList = new ArrayList<>();
        SingleShotLocationProvider.requestSingleUpdate(DriverActivity.this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        try {
                            mylocation = new LatLng(location.latitude,location.longitude);
                          final String  city = Utils.LocationCity(DriverActivity.this,location.latitude,location.longitude);

                          new Thread(new Runnable() {
                              @Override
                              public void run() {

                                  try{
                                      SignInNetworkAction getPassengerBookedNetworkAction=  new SignInNetworkAction(city
                                              ,"GetAvailableCars");
                                      Response response = NetworkContentHelper.ApiGatewayCaller(getPassengerBookedNetworkAction);
                                      String  result = response.body().string();

                                      App. Log("NetworkContentHelper "+result);

                                      availableCarsList = new ArrayList<>();
                                      AvailableCars[] availableCarsArray = new Gson().fromJson(result,AvailableCars[].class);


                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              for(AvailableCars availableCars : availableCarsArray){

                                                  availableCarsList.add(availableCars);
                                                  mMap.addMarker(new MarkerOptions().position(
                                                         Utils.LatLonOBjFromString(availableCars.getLocation())
                                                  )
                                                          .title(availableCars.getCarModel()).icon(Utils.CarIconSmall(
                                                                  DriverActivity.this
                                                          )));
                                              }
                                              initAvailableCarList();
                                          }
                                      });}
                                  catch (Exception ex){}
                              }
                          }).start();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    private void initAvailableCarList(){
        LinearLayoutManager listManager = new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false);

        availableCarsRecyclerView.setLayoutManager(listManager);

        availableCarsRecyclerView.setAdapter(new AvailableContentAdapter());
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public Button startTime,endTime;
        public TextView locationTextView,workingHoursTextView;
        public View card;
        public ViewHolder(View itemView) {
            super(itemView);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            workingHoursTextView = itemView.findViewById(R.id.workingHoursTextView);
            card = itemView.findViewById(R.id.card);
        }
    }
    private class AvailableContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private static final int HEADER = 0;
        private static final int CONTENT = 1;
        private int previousSelected = -1;
        public AvailableContentAdapter(){
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
          return new ViewHolder(LayoutInflater.from(container.getContext()).inflate(R.layout.available_car_item, container, false));


        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
           final AvailableCars availableCars = availableCarsList.get(position);


            double distance = SphericalUtil.computeDistanceBetween(Utils.LatLonOBjFromString(availableCars.getLocation()), mylocation);
            holder.startTime.setText(availableCars.getPickUpTime());
            holder.endTime.setText(availableCars.getReturnTime());
            holder.locationTextView.setText(availableCars.getLocationName()+" "+Utils.RoundOff(distance/1000.00)+" km away");
            holder.workingHoursTextView.setText((Integer.parseInt(availableCars.getReturnTime().split(":")[0]
                    )
            -Integer.parseInt(availableCars.getPickUpTime().split(":")[0]
            ))+" working hours");

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Utils.SaveString(App.CarRequestData,DriverActivity.this,new Gson().toJson(availableCars));

                    Intent intent = new Intent(DriverActivity.this,DriverCodeActivity.class);
                    intent.putExtra(App.Content,new Gson().toJson(availableCars));
                   startActivity(intent);
                   finish();

                }
            });



        }
        @Override
        public int getItemCount() {
            return availableCarsList.size();
        }
    }
}