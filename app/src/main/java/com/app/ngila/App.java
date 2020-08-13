package com.app.ngila;

import android.app.Application;

import pl.tajchert.nammu.Nammu;

public class App extends Application {

    public static  final String Content = "Content";
    @Override
    public void onCreate() {
        super.onCreate();
        Nammu.init(this);



    }
}
