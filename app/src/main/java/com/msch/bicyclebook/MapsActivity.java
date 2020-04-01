package com.msch.bicyclebook;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback {

    public GoogleMap mMap;
    public CharSequence spentTime;
    private Button createRouteBtn;
    private Button saveRouteBtn;
    public Chronometer timer;
    public ArrayList<LatLng> route = new ArrayList<LatLng>();
    private String FILENAME;
    private File fileDir;


    public void createSavedRoutesFile() {
        FILENAME = "/savedRoutes.json";
        fileDir = new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/" + getPackageName());
        File fullPath = new File(fileDir + FILENAME);

        if (!fullPath.exists()) {
            File savedRouteFile = new File(fileDir, FILENAME);
            try {
                savedRouteFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
            for (int i = 0; i < arraySize; i++) {
                point = route.get(i);
                latitude = point.latitude;
                longitude = point.longitude;
                routeLine.add(new LatLng(latitude, longitude));
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
                    long eachSecond = elapsedMillis / 1000;
                    int interval = 10;

                    if ((eachSecond % interval == 0) && (eachSecond >= interval)) {
                        writeCoordinates();
                    }
                }
            });

            if (isCreateButtonPressed) {
                createRouteBtn.setText("Финиш");
                isCreateButtonPressed = false;
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
                saveRouteBtn.setEnabled(false);
            } else {
                createRouteBtn.setText("Старт");
                isCreateButtonPressed = true;
                timer.stop();
                spentTime = timer.getText();
                writeCoordinates();
                mMap.addMarker(new MarkerOptions().position(currentPosition).title("Финиш"));
                drawRoute(route);
                saveRouteBtn.setEnabled(true);
            }
        }
    };

    View.OnClickListener saveRouteBtnListener = new View.OnClickListener() {
        Route_info NewRoute = new Route_info();
        @Override
        public void onClick(View v) {
            AlertDialog.Builder saveRouteDialog = new AlertDialog.Builder(MapsActivity.this);

            saveRouteDialog.setTitle("Введите имя маршрута");
            saveRouteDialog.setMessage("Имя маршрута");
            final EditText input = new EditText(MapsActivity.this);

            saveRouteDialog.setView(input);

            saveRouteDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String routeName = input.getText().toString();

                    NewRoute.setRouteId(/*routeId*/ 2);
                    NewRoute.setRouteName(routeName);
                    NewRoute.setTraveledDistance(/*traveledDistance*/ 2);
                    NewRoute.setRouteTime(spentTime);
                    NewRoute.setRoutePoints(route);

                    saveRouteBtn.setEnabled(false);

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Log.i("GSON",gson.toJson(NewRoute));

                    try {
                        FileOutputStream outputStream = new FileOutputStream(fileDir + FILENAME, true);
                        outputStream.write(gson.toJson(NewRoute).getBytes());
                        outputStream.close();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            saveRouteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Toast.makeText(getApplicationContext(),"Отменено",Toast.LENGTH_SHORT).show();
                }
            });

            saveRouteDialog.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createRouteBtn = findViewById(R.id.start);
        saveRouteBtn = findViewById(R.id.saveRouteBtn);
        createRouteBtn.setOnClickListener(createRouteBtnListener);
        saveRouteBtn.setOnClickListener(saveRouteBtnListener);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        createSavedRoutesFile();
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
