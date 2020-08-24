package com.app.ngila;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.app.ngila.data.AvailableCars;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.locationhandler.SingleShotLocationProvider;
import com.app.ngila.network.NetworkContentHelper;
import com.app.ngila.network.actions.AddDriverLocation;
import com.app.ngila.network.actions.DriverCarOwnerContract;
import com.app.ngila.network.actions.DriverWaiting;
import com.app.ngila.network.actions.SignInNetworkAction;
import com.app.ngila.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

import okhttp3.Response;

public class LocationService extends Service {
    NgilaUser ngilaUser;
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ngilaUser = Utils.GetNgilaUser(this);


        Handler handler = new Handler();
        int delay = 1000*45; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){

                String BookedDriverAcceptedHiredDataString = Utils.GetString(App.BookedDriverAcceptedHiredData,
                        LocationService.this);

                String PickingUpPassengerDataString = Utils.GetString(App.PickingUpPassenger,
                        LocationService.this);


                if(BookedDriverAcceptedHiredDataString!=null){
                    DriverCarOwnerContract acceptedDriver = new Gson().fromJson(BookedDriverAcceptedHiredDataString, DriverCarOwnerContract.class);


                    SingleShotLocationProvider.requestSingleUpdate(LocationService.this,
                            new SingleShotLocationProvider.LocationCallback() {
                                @Override
                                public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {

                                    AddDriverLocation addDriverLocation = new AddDriverLocation(
                                            ngilaUser.getPhoneNumber(),
                                            Utils.LatLonOBjToString(new LatLng(location.latitude, location.longitude)
                                    ));
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                NetworkContentHelper.AddContent(LocationService.this,addDriverLocation);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }).start();

                                }
                            });
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

        return START_STICKY;
    }


}
