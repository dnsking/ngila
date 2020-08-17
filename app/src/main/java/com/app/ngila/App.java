package com.app.ngila;

import android.app.Application;

import pl.tajchert.nammu.Nammu;

public class App extends Application {

    public static  final String Content = "Content";

    public static  final String Prefs = "com.app.ngila.prefs";
    public static  final String User = "user";
    public static  final String Password = "Password";
    public static  final String UserDetails = "UserDetails";
    public static  final String AccountType = "AccountType";


    public static  final String AccountTypeCarOwner = "AccountTypeCarOwner";
    public static  final String AccountTypeDriver = "AccountTypeDriver";
    public static  final String AccountTypePassenger = "AccountTypePassenger";


    @Override
    public void onCreate() {
        super.onCreate();
        Nammu.init(this);



    }
}
