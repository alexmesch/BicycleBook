package com.msch.bicyclebook;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback {

    public GoogleMap mMap;
    public CharSequence spentTime;
    private Button createRouteBtn;
    public Chronometer timer;
    public ArrayList<LatLng> route = new ArrayList<LatLng>();

    View.OnClickListener createRouteBtnListener = new View.OnClickListener() {
        private boolean isCreateButtonPressed = true;
        private Location coordinates;
        private LatLng currentPosition;

        private void writeCoordinates() {
            coordinates = mMap.getMyLocation();
            currentPosition = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
            route.add(currentPosition);
        }

        public void drawRoute(final ArrayList<LatLng> route) {
            double latitude, longitude;
            LatLng point;
            int arraySize = route.size();
            PolylineOptions routeLine = new PolylineOptions();
            routeLine.color(Color.RED);
            for (int i=0; i < arraySize; i++) {
                point = route.get(i);
                latitude = point.latitude;
                longitude = point.longitude;
                routeLine.add(new LatLng(latitude,longitude));
            }
            Polyline polyline = mMap.addPolyline(routeLine);
        }

        @Override
        public void onClick(View v) {
            timer = findViewById(R.id.timer);

            writeCoordinates();
            mMap.addMarker(new MarkerOptions().position(currentPosition).title("Старт"));

            timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    long elapsedMillis = SystemClock.elapsedRealtime() - timer.getBase();
                    long eachSecond = elapsedMillis/1000;
                    int interval = 10;

                    if ((eachSecond % interval == 0) && (eachSecond >= interval)) {
                        writeCoordinates();
                    }
                }
            });

            if (isCreateButtonPressed) {
                route.clear();
                createRouteBtn.setText("Финиш");
                isCreateButtonPressed = false;
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
            }
            else {
                createRouteBtn.setText("Старт");
                isCreateButtonPressed = true;
                timer.stop();
                spentTime = timer.getText();
                //System.out.println(route); //DEBUG
                writeCoordinates();
                mMap.addMarker(new MarkerOptions().position(currentPosition).title("Финиш"));
                drawRoute(route);
                //route.clear();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createRouteBtn = findViewById(R.id.start);
        createRouteBtn.setOnClickListener(createRouteBtnListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        } else {
            Toast.makeText(this, "Вы не разрешили геолокацию!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Текущая позиция:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Перемещаемся в текущее положение...", Toast.LENGTH_LONG).show();
        return false;
    }

    public void login(View v) {
        Intent SignIn = new Intent(this, SignIn.class);
        startActivity(SignIn);
    }
}
