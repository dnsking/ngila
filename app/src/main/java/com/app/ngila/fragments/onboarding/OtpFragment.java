package com.app.ngila.fragments.onboarding;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ngila.JoinLoginActivity;
import com.app.ngila.R;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.utils.Utils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

public class OtpFragment extends Fragment implements Step {


    private NgilaUser ngilaUser;
    private TextView fourDigitCodeTxtView,resendCodeIn;
    private OtpView otp_view;
    private ExtendedFloatingActionButton resend;
    private View loadingView;

    public void setNgilaUser(NgilaUser ngilaUser) {
        this.ngilaUser = ngilaUser;
        fourDigitCodeTxtView.setText("A verification code was sent to "+ngilaUser.getPhoneNumber());
    }

    public OtpFragment() {
        // Required empty public constructor
    }

    public static OtpFragment newInstance() {
        OtpFragment fragment = new OtpFragment();

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {

                loadingView.setVisibility(View.VISIBLE);
                otp_view.setEnabled(false);
                ((JoinLoginActivity) getActivity()). match( otp);
            }
        });

        resendCodeIn.setVisibility(View.VISIBLE);

        resend.setEnabled(false);
        new android.os.CountDownTimer(60*1000*2,50){
            @Override
            public void onTick(long millisUntilFinished) {
                try{

                    resendCodeIn.setText(Utils.ConvertSecondsToHMmSs(
                            millisUntilFinished));
                }
                catch (Exception ex){

                }
            }

            @Override
            public void onFinish() {
                try{

                    resend.setEnabled(true);
                    resendCodeIn.setVisibility(View.INVISIBLE);
                }
                catch (Exception ex){

                }

            }
        }.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_otp, container, false);
        fourDigitCodeTxtView = view.findViewById(R.id.fourDigitCodeTxtView);
        otp_view = view.findViewById(R.id.otp_view);
        resend = view.findViewById(R.id.resend);
        loadingView = view.findViewById(R.id.loadingView);
        resendCodeIn = view.findViewById(R.id.resendCodeIn);
        return view;
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}