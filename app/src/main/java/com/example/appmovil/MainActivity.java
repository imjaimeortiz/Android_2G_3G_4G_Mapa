package com.example.appmovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.icu.util.TimeZone;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.LocationCallback;

import java.time.Clock;

public class MainActivity extends AppCompatActivity {
    private static final String TAG=AppCompatActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }


    public void onClick2g(View v){
        Intent intent = new Intent( this,ActividadMaps.class);
        intent.putExtra("tecnologia", 2);
        startActivity(intent);
    }

    public void onClick3g(View v){
        Intent intent = new Intent( this,ActividadMaps.class);;
        intent.putExtra("tecnologia", 3);
        startActivity(intent);
    }

    public void onClick4g(View v){
        Intent intent = new Intent( this,ActividadMaps.class);
        intent.putExtra("tecnologia", 4);
        startActivity(intent);
    }

}
