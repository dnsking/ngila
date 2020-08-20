package com.app.ngila;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.ngila.data.NgilaUser;
import com.app.ngila.utils.Utils;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class DriverCodeActivity extends AppCompatActivity {

    private NgilaUser ngilaUser;
    private ImageView qrImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ngilaUser = Utils.GetNgilaUser(this);
        ngilaUser.setPassword(null);
        setContentView(R.layout.activity_driver_code);

        qrImageView = findViewById(R.id.qrImageView);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        QRGEncoder qrgEncoder = new QRGEncoder(new Gson().toJson(ngilaUser),  QRGContents.Type.TEXT,smallerDimension);
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);

        qrImageView.setImageBitmap(qrgEncoder.getBitmap());
    }
}