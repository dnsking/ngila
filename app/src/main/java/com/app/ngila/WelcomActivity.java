package com.app.ngila;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.app.ngila.utils.Utils;

public class WelcomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        findViewById(R.id.joinButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomActivity.this,JoinLoginActivity.class);
                intent.putExtra(App.Content,
                        ((RadioButton)findViewById(R.id.accPassenger)).isChecked()
                                ?App.AccountTypePassenger:
                                ((RadioButton)findViewById(R.id.accDriver)).isChecked()?App.AccountTypeDriver:App.AccountTypeCarOwner
                        );
                startActivity(intent);
                finish();
            }
        });
        if(Utils.GetUserName(this)!=null){
            if(Utils.GetAccountType(this).equals(App.AccountTypeCarOwner)){

                Intent intent = new Intent(WelcomActivity.this,CarOwnerActivity.class);
                startActivity(intent);
            }
            else if(Utils.GetAccountType(this).equals(App.AccountTypeDriver)){

               Intent intent = new Intent(WelcomActivity.this, DriverActivity.class);
               startActivity(intent);
            }
            else if(Utils.GetAccountType(this).equals(App.AccountTypePassenger)){

                Intent intent = new Intent(WelcomActivity.this,PassengerActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}