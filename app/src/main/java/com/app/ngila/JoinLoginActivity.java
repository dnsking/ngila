package com.app.ngila;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.app.ngila.data.NgilaUser;
import com.app.ngila.fragments.carowner.CarOwnerJoinFragment;
import com.app.ngila.fragments.driver.DriverJoinFragment;
import com.app.ngila.fragments.onboarding.BaseNewAccountFragment;
import com.app.ngila.fragments.onboarding.OtpFragment;
import com.app.ngila.fragments.passenger.PassengerJoinFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class JoinLoginActivity extends AppCompatActivity {

    private StepperLayout mStepperLayout;
    private String accType;
    private BaseNewAccountFragment baseNewAccountFragment;
    private OtpFragment otpFragment;
    private NgilaUser ngilaUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accType = getIntent().getStringExtra(App.Content);
        setContentView(R.layout.activity_join_login);


        mStepperLayout.setListener(new StepperLayout.StepperListener() {
            @Override
            public void onCompleted(View completeButton) {

            }

            @Override
            public void onError(VerificationError verificationError) {

            }

            @Override
            public void onStepSelected(int newStepPosition) {
                if(newStepPosition==1){
                    ngilaUser = baseNewAccountFragment.getUser();
                    otpFragment.setNgilaUser(ngilaUser);
                }


            }

            @Override
            public void onReturn() {

            }
        });

        mStepperLayout.setAdapter(new StepperAdapter(getSupportFragmentManager(), this));
    }


    public  class StepperAdapter extends AbstractFragmentStepAdapter {

        public StepperAdapter(FragmentManager fm, Context context) {
            super(fm, context);
        }

        @Override
        public Step createStep(int position) {
            if(position==0){
                if(accType.equals(App.AccountTypePassenger)){
                    baseNewAccountFragment = new PassengerJoinFragment();
                }

                else if(accType.equals(App.AccountTypeDriver)){
                    baseNewAccountFragment = new DriverJoinFragment();
                }


                if(accType.equals(App.AccountTypeCarOwner)){
                    baseNewAccountFragment = new CarOwnerJoinFragment();
                }
                return baseNewAccountFragment;
            }
            else if(position==1){
                if(otpFragment==null){
                    otpFragment = new OtpFragment();
                }
                return otpFragment;
            }



            return null;
        }
        @NonNull
        @Override
        public StepViewModel getViewModel(@IntRange(from = 0) int position) {
            StepViewModel.Builder builder = new StepViewModel.Builder(context);
            switch (position) {
                case 1:
                    builder.setEndButtonLabel("Verify");
                    break;
            }
            return builder.create();
        }

        @Override
        public int getCount() {
            return 2;
        }}
}