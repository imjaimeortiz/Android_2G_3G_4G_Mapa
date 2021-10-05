package com.example.appmovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActividadMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public final static int MY_PHONE_STATE_PERMISSION = 0;
    private static final String TAG = AppCompatActivity.class.getSimpleName();
    private TelephonyManager telephonyManager;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback callback;
    private Location location;
    private boolean gsm = false;
    private boolean wcdm = false;
    private boolean lte = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        Bundle extras = getIntent().getExtras();
        if (!extras.isEmpty()) {
            Object extra = extras.get("tecnologia");
            if (extra instanceof Integer) {
                if (extra == (Integer) 2) {
                    gsm = true;
                }

                if (extra == (Integer) 3) {
                    wcdm = true;
                }
                if (extra == (Integer) 4) {
                    lte = true;
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                double latitud = location.getLatitude();
                double longitud = location.getLongitude();
                LatLng ubicacion = new LatLng(latitud,longitud);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));
                int level= getAllCells();
                imprimirPuntos(latitud, longitud, level);
            }
        };
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

    }


    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(callback);
    }

    private void showLastLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(50);
        fusedLocationClient.requestLocationUpdates(locationRequest, callback, null);
    }


    private void imprimirPuntos(double latitud, double longitud, int level) {
        switch (level)
        {
            case 0:
                mMap.addCircle(new CircleOptions().center(new LatLng(latitud,  longitud)).radius(30).fillColor(Color.BLACK).strokeWidth(3));
                break;

                case 1:
                mMap.addCircle(new CircleOptions().center(new LatLng(latitud,  longitud)).radius(30).fillColor(Color.RED).strokeWidth(3));
                break;

            case 2:
                mMap.addCircle(new CircleOptions().center(new LatLng(latitud,  longitud)).radius(30).fillColor(Color.rgb(255, 128, 0)).strokeWidth(3));
                break;

            case 3:
                mMap.addCircle(new CircleOptions().center(new LatLng(latitud,  longitud)).radius(30).fillColor(Color.YELLOW).strokeWidth(3));
                break;

            case 4:
                mMap.addCircle(new CircleOptions().center(new LatLng(latitud,  longitud)).radius(30).fillColor(Color.GREEN).strokeWidth(3));
                break;
        }
    }

    public int getAllCells() {
        StringBuilder text = new StringBuilder();
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Integer.parseInt(null);
        }
        List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
        text.append("Found ").append(cellInfoList.size()).append(" cells\n");
        int contador=0;

        for (CellInfo info : cellInfoList) {
            if ((info instanceof CellInfoGsm) && (gsm = true)) {
                CellInfoGsm cellInfoGsm = (CellInfoGsm) info;
                CellIdentityGsm id = cellInfoGsm.getCellIdentity();
                text.append("GSM ID:{cid: } ").append((id.getCid()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    text.append(" mcc: ").append(id.getMccString());
                } else text.append(" mcc: ").append(id.getMcc());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    text.append(" mnc: ").append(id.getMncString());
                } else text.append(" mnc: ").append(id.getMnc());
                text.append((" lac: ")).append(id.getLac());
                text.append("} Level ").append(cellInfoGsm.getCellSignalStrength().getLevel()).append("\n");
                if (cellInfoGsm.getCellSignalStrength().getLevel() > contador)
                {
                    contador = cellInfoGsm.getCellSignalStrength().getLevel();
                }



            } else if ((info instanceof CellInfoWcdma) && (wcdm = true)) {
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) info;
                CellIdentityWcdma id = cellInfoWcdma.getCellIdentity();
                text.append("WCDM ID:{cid: } ").append((id.getCid()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    text.append(" mcc: ").append(id.getMccString());
                } else text.append(" mcc: ").append(id.getMcc());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    text.append(" mnc: ").append(id.getMncString());
                } else text.append(" mnc: ").append(id.getMnc());
                text.append((" lac: ")).append(id.getLac());
                text.append("} Level ").append(cellInfoWcdma.getCellSignalStrength().getLevel()).append("\n");
                if (cellInfoWcdma.getCellSignalStrength().getLevel() > contador)
                {
                    contador = cellInfoWcdma.getCellSignalStrength().getLevel();
                }


            } else if ((info instanceof CellInfoLte) && (lte = true)) {
                CellInfoLte cellInfoLte = (CellInfoLte) info;
                CellIdentityLte id = cellInfoLte.getCellIdentity();
                text.append("WCDM ID:{cid: } ").append((id.getCi()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    text.append(" mcc: ").append(id.getMccString());
                } else text.append(" mcc: ").append(id.getMcc());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    text.append(" mnc: ").append(id.getMncString());
                } else text.append(" mnc: ").append(id.getMnc());
                text.append((" lac: ")).append(id.getTac());
                text.append("} Level ").append(cellInfoLte.getCellSignalStrength().getLevel()).append("\n");
                if (cellInfoLte.getCellSignalStrength().getLevel() > contador)
                {
                    contador = cellInfoLte.getCellSignalStrength().getLevel();
                }
            }
        }
        return contador;
    }








    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showPosition();
        showLastLocation();
        comprobarUbi();
    }


    private void comprobarUbi()
    {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLocationAvailability().addOnSuccessListener(new OnSuccessListener<LocationAvailability>() {
            @Override
            public void onSuccess(LocationAvailability locationAvailability)
            {
                if (locationAvailability.isLocationAvailable())
                {

                }
                else Toast.makeText(getApplicationContext(), "Se debería activar la ubicación", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPosition() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 0);
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(mMap);
                } else {
                    Toast.makeText(this, "Se necesita poder accerder a la ubicación para que la aplicación funcione", Toast.LENGTH_LONG).show();
                }
            }

        }

    }
}
