package com.app.ngila;

import android.app.Application;
import android.util.Log;

import com.app.ngila.network.actions.AcceptedDriver;
import com.google.android.libraries.places.api.Places;

import pl.tajchert.nammu.Nammu;

public class App extends Application {

    public static  final String Content = "Content";

    public static  final String Prefs = "com.app.ngila.prefs";
    public static  final String User = "user";
    public static  final String Password = "Password";
    public static  final String UserData = "UserData";
    public static  final String UserDetails = "UserDetails";
    public static  final String AccountType = "AccountType";
    public static  final String Status = "Status";

    public static  final String BookedDriver = "BookedDriver";
    public static  final String BookedDriverWaiting = "BookedDriverWaiting";
    public static  final String BookedDriverAccepted = "BookedDriverAccepted";
    public static  final String BookedDriverAcceptedData = "BookedDriverAcceptedData";
    public static  final String BookedDriverAcceptedHiredData = "BookedDriverAcceptedHiredData";
    public static  final String BookedDriverAcceptedHired = "BookedDriverAcceptedHired";
    public static  final String PickingUpPassenger = "PickingUpPassenger";


    public static  final String Order = "Order";


    public static  final String CarRequestData= "CarRequestData";
    public static  final String CarOwner = "CarOwner";
    public static  final String Passenger = "Passenger";

    public static  final String Status_CarBooked = "Status_CarBooked";
    public static  final String Status_Passenger = "Status_Passenger";

    public static  final String DriverContractStarted = "Contract Started";


    public static  final String AccountTypeCarOwner = "AccountTypeCarOwner";
    public static  final String AccountTypeDriver = "AccountTypeDriver";
    public static  final String AccountTypePassenger = "AccountTypePassenger";


    public static  final String AcceptedDriverBroadcast = "AcceptedDriverBroadcast";
    public static  final String DriverCarOwnerContractBroadcast = "DriverCarOwnerContractBroadcast";



    public static boolean IsDebug = true;
    private static final String TAG = "Ngila Client";

    public static void Log(String msg){
        if(App.IsDebug){
            int maxLogSize = 1000;
            if(msg.length()>maxLogSize)
                for(int i = 0; i <= msg.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i+1) * maxLogSize;
                    end = end > msg.length() ? msg.length() : end;
                    Log.i(TAG, msg.substring(start, end));
                }
            else
                Log.i(TAG, msg);
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Nammu.init(this);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
    }

    public class Urls{
        public static final String Content = "https://e5t8cxjmc8.execute-api.us-east-1.amazonaws.com/NgilaStage";
    }
}
