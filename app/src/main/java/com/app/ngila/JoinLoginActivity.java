package com.app.ngila;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.ngila.data.Authentication;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.fragments.carowner.CarOwnerJoinFragment;
import com.app.ngila.fragments.driver.DriverJoinFragment;
import com.app.ngila.fragments.onboarding.BaseNewAccountFragment;
import com.app.ngila.fragments.onboarding.OtpFragment;
import com.app.ngila.fragments.passenger.PassengerJoinFragment;
import com.app.ngila.network.NetworkContentHelper;
import com.app.ngila.network.actions.MatchCodeNetworkAction;
import com.app.ngila.network.actions.SignInNetworkAction;
import com.app.ngila.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import okhttp3.Response;

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

        mStepperLayout=findViewById(R.id.stepperLayout);

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
                    sendOpt();
                }


            }

            @Override
            public void onReturn() {

            }
        });

        mStepperLayout.setAdapter(new StepperAdapter(getSupportFragmentManager(), this));
    }


    public void sendOpt(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NetworkContentHelper.AddContent(JoinLoginActivity.this,
                            new SignInNetworkAction(ngilaUser.getPhoneNumber()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void match(String opt){
        new Thread(new Runnable() {
            @Override
            public void run() {



                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    try {
                                    final String token = task.getResult().getToken();

                                    App.Log("Device Id "+token);

                                    ngilaUser.setDeviceId(token);
                                    MatchCodeNetworkAction matchCodeNetworkAction=  new MatchCodeNetworkAction(ngilaUser.getPhoneNumber(),opt,ngilaUser);
                                    Response response = NetworkContentHelper.ApiGatewayCaller(matchCodeNetworkAction);

                                    String result = response.body().string();
                                    App. Log("MatchKey "+result);

                                    Authentication authenticationResult = new Gson().fromJson(result,Authentication.class);

                                    Utils.SaveAccountType(JoinLoginActivity.this,accType);
                                    Utils.SaveUserName(JoinLoginActivity.this,ngilaUser.getPhoneNumber());
                                    Utils.SaveString(App.UserData,JoinLoginActivity.this,new Gson().toJson(ngilaUser));

                                    if(accType.equals(App.AccountTypeCarOwner)){
                                        Intent intent = new Intent(JoinLoginActivity.this,CarOwnerActivity.class);
                                        startActivity(intent);
                                    }
                                    else if(accType.equals(App.AccountTypeDriver)){
                                        Intent intent = new Intent(JoinLoginActivity.this, DriverActivity.class);
                                        startActivity(intent);
                                    }
                                    else if(accType.equals(App.AccountTypePassenger)){
                                        Intent intent = new Intent(JoinLoginActivity.this,PassengerActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                }
                            });


            }
        }).start();
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