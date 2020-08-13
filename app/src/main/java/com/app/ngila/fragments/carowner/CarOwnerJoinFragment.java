package com.app.ngila.fragments.carowner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ngila.R;

public class CarOwnerJoinFragment extends Fragment {


    public CarOwnerJoinFragment() {
        // Required empty public constructor
    }

    public static CarOwnerJoinFragment newInstance() {
        CarOwnerJoinFragment fragment = new CarOwnerJoinFragment();
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
        return inflater.inflate(R.layout.fragment_car_owner_join, container, false);
    }
}