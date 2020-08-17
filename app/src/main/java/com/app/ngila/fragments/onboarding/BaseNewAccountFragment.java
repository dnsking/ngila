package com.app.ngila.fragments.onboarding;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ngila.R;
import com.app.ngila.data.NgilaUser;
import com.google.android.material.textfield.TextInputEditText;
import com.stepstone.stepper.Step;

public abstract class BaseNewAccountFragment extends Fragment implements Step {

    private TextInputEditText firstNameTextInputEditText
            ,lastNameTextInputEditText,
            phoneNumberTextInputEditText,
            passwordTextInputEditText
            ,repeatpasswordTextInputEditText,
            carBrandAndModelTextInputEditTextHolder,
            numberPlateTextInputEditText;

    public BaseNewAccountFragment() {
        // Required empty public constructor
    }


    public abstract NgilaUser getUser();
}