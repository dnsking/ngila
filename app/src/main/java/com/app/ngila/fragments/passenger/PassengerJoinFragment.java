package com.app.ngila.fragments.passenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ngila.R;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.fragments.onboarding.BaseNewAccountFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

public class PassengerJoinFragment extends BaseNewAccountFragment implements Step {
    private TextInputEditText firstNameTextInputEditText
            ,lastNameTextInputEditText,
            phoneNumberTextInputEditText,
            passwordTextInputEditText
            ,repeatpasswordTextInputEditText;

    public PassengerJoinFragment() {
        // Required empty public constructor
    }

    @Override
    public NgilaUser getUser() {
        NgilaUser ngilaUser = new NgilaUser();
        ngilaUser.setFirstName(firstNameTextInputEditText.getText().toString());
        ngilaUser.setLastName(lastNameTextInputEditText.getText().toString());
        ngilaUser.setPhoneNumber(phoneNumberTextInputEditText.getText().toString());
        ngilaUser.setPassword(passwordTextInputEditText.getText().toString());
        return ngilaUser;
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
        View view = inflater.inflate(R.layout.fragment_passenger_join, container, false);
        firstNameTextInputEditText = view.findViewById(R.id.firstNameTextInputEditText);
        lastNameTextInputEditText = view.findViewById(R.id.lastNameTextInputEditText);
        phoneNumberTextInputEditText = view.findViewById(R.id.phoneNumberTextInputEditText);
        passwordTextInputEditText = view.findViewById(R.id.passwordTextInputEditText);
        repeatpasswordTextInputEditText = view.findViewById(R.id.repeatpasswordTextInputEditText);
        return view;
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if(firstNameTextInputEditText.getText()==null
                ||firstNameTextInputEditText.getText().toString().length()<1){

            return new VerificationError("Enter First Name");
        }
        else if(lastNameTextInputEditText.getText()==null
                ||lastNameTextInputEditText.getText().toString().length()<1){

            return new VerificationError("Enter Last Name");
        }
        else if(phoneNumberTextInputEditText.getText()==null
                ||phoneNumberTextInputEditText.getText().toString().length()<1){

            return new VerificationError("Enter Phone Number");
        }

        else if(passwordTextInputEditText.getText()==null
                ||passwordTextInputEditText.getText().toString().length()<1){

            return new VerificationError("Enter Password");
        }

        else if(!passwordTextInputEditText.getText().toString().equals(repeatpasswordTextInputEditText.getText()
                .toString())){

            return new VerificationError("Repeat Password Does Not Match");
        }
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}