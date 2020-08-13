package com.app.ngila.fragments.passenger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ngila.R;

public class PassengerJoinFragment extends Fragment {


    public PassengerJoinFragment() {
        // Required empty public constructor
    }

    public static PassengerJoinFragment newInstance() {
        PassengerJoinFragment fragment = new PassengerJoinFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_passenger_join, container, false);
    }
}