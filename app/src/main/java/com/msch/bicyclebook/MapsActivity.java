package com.msch.bicyclebook;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;


public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleMap.OnMapLoadedCallback
{
    public GoogleMap mMap;
    public Location coordinates;
    public LatLng currentPosition;
    public CharSequence spentTime;
    private Button createRouteBtn;
    private Button saveRouteBtn;
    private Button forgetRouteBtn;
    public Chronometer timer;
    public TextView distance;
    public ArrayList<LatLng> route = new ArrayList<LatLng>();
    public final File filePath = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.msch.bicyclebook/savedRoutes/");
    public int[] routeColor = new int[3];


    //************************************* UTILITY_FUNCTIONS ************************************//

    public int randomizeColor() {
        Random rnd = new Random();
        int colorCode = rnd.nextInt(255);
        return colorCode;
    }

    public String randomizeId() {

        String randomizedId;
        UUID randId = UUID.randomUUID();
        randomizedId = randId.toString();

        return randomizedId;
    }

    public String readRouteFile(String routeFileName) {
        String routeInfo = "";
        try {
            FileInputStream inputStream = new FileInputStream(filePath + "/" + routeFileName);
            int data = inputStream.read();
            char content = '\0';

            while (data != -1) {
                content = (char) data;
                data = inputStream.read();
                routeInfo = routeInfo + content;
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return routeInfo;
    }

    public int calculateDistance(ArrayList<LatLng> route) {
        float[] distance = new float[10];
        int completeDistance;
        LatLng startPoint, finishPoint;
        startPoint = route.get(0);
        finishPoint = route.get(route.size() - 1);
        Location.distanceBetween(startPoint.latitude, startPoint.longitude, finishPoint.latitude, finishPoint.longitude, distance);
        completeDistance = (int) distance[0];
        return (completeDistance);
    }

    //************************************* MAP_OPERATIONS ************************************//

    private void drawRoute(final ArrayList<LatLng> route, final int[] routeColor) {
        LatLng point;

        PolylineOptions routeLine = new PolylineOptions();
        routeLine.color(Color.rgb(routeColor[0], routeColor[1], routeColor[2]));

        for (int i = 0; i < route.size(); i++) {
            point = route.get(i);
            routeLine.add(new LatLng(point.latitude, point.longitude));
        }
        Polyline polyline = mMap.addPolyline(routeLine);
    }

    public void drawExistingRoute(String selectedRoute) {
        ArrayList<LatLng> routePoints = new ArrayList<>();
        Route_info completeRouteInfo = new Route_info();
        String routeInfo = "";
        LatLng start_point, finish_point;

        routeInfo = readRouteFile(selectedRoute);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        completeRouteInfo = gson.fromJson(routeInfo, Route_info.class);
        routePoints = completeRouteInfo.getRoutePoints();
        routeColor = completeRouteInfo.getRouteColor();

        try {
            drawRoute(routePoints,routeColor);
            start_point = routePoints.get(0);
            finish_point = routePoints.get(routePoints.size() - 1);
            mMap.addMarker(new MarkerOptions().position(start_point).title("Старт").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.addMarker(new MarkerOptions().position(finish_point).title("Финиш").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start_point,15));
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getApplicationContext(),"Файл маршрута поврежден!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    View.OnClickListener createRouteBtnListener = new View.OnClickListener() {
        private boolean isCreateButtonPressed = true;

        private void writeCoordinates() {
            coordinates = mMap.getMyLocation();
            currentPosition = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
            route.add(currentPosition);
        }

       @Override
        public void onClick(View v) {
            timer = findViewById(R.id.timer);
            distance = findViewById(R.id.distance);
            Drawable stop = getResources().getDrawable(R.drawable.ic_stop);
            Drawable start = getResources().getDrawable(R.drawable.ic_play_arrow);
            try {
                writeCoordinates();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,15));
                mMap.addMarker(new MarkerOptions().position(currentPosition).title("Старт").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        long elapsedMillis = SystemClock.elapsedRealtime() - timer.getBase();
                        long eachSecond = elapsedMillis / 1000;
                        int interval = 5;

                        if ((eachSecond % interval == 0) && (eachSecond >= interval)) {
                            distance.setText(calculateDistance(route) + "м");
                            writeCoordinates();;
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,15));
                            drawRoute(route,routeColor);
                        }
                    }
                });

                if (isCreateButtonPressed) {
                    for (int i=0; i<3; i++) {
                        routeColor[i] = randomizeColor();
                    }
                    createRouteBtn.setBackground(stop);
                    isCreateButtonPressed = false;
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.start();
                    saveRouteBtn.setVisibility(View.INVISIBLE);
                    forgetRouteBtn.setVisibility(View.INVISIBLE);
                } else {
                    createRouteBtn.setBackground(start);
                    isCreateButtonPressed = true;
                    timer.stop();
                    spentTime = timer.getText();
                    writeCoordinates();
                    mMap.addMarker(new MarkerOptions().position(currentPosition).title("Финиш").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    saveRouteBtn.setVisibility(View.VISIBLE);
                    forgetRouteBtn.setVisibility(View.VISIBLE);
                }
            } catch(Exception e) {
                if (coordinates == null) {
                    Toast.makeText(getApplicationContext(),"Геоданные не работают!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    };

    View.OnClickListener forgetRouteBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMap.clear();
            timer.setText("00:00");
            saveRouteBtn.setVisibility(View.INVISIBLE);
            forgetRouteBtn.setVisibility(View.INVISIBLE);
            route.clear();
        }
    };

    View.OnClickListener saveRouteBtnListener = new View.OnClickListener() {
        Route_info NewRoute = new Route_info();
        String routeFileName;
        @Override
        public void onClick(View v) {
            routeFileName = randomizeId();
            AlertDialog.Builder saveRouteDialog = new AlertDialog.Builder(MapsActivity.this);

            saveRouteDialog.setTitle("Введите имя маршрута");
            saveRouteDialog.setMessage("Имя маршрута");
            final EditText input = new EditText(MapsActivity.this);
            saveRouteDialog.setView(input);

            saveRouteDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String routeName = input.getText().toString();
                    NewRoute.setRouteId (routeFileName);
                    NewRoute.setRouteName(routeName);
                    NewRoute.setRouteColor(routeColor);
                    NewRoute.setTraveledDistance(calculateDistance(route));
                    NewRoute.setRouteTime(spentTime.toString());
                    NewRoute.setRoutePoints(route);

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Log.i("GSON",gson.toJson(NewRoute));
                    try {
                        FileWriter writer = new FileWriter(new File(filePath,routeFileName));
                        gson.toJson(NewRoute, writer);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    saveRouteBtn.setVisibility(View.INVISIBLE);
                    forgetRouteBtn.setVisibility(View.INVISIBLE);
                    mMap.clear();
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
        setContentView(R.layout.map_tab);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createRouteBtn = findViewById(R.id.start);
        saveRouteBtn = findViewById(R.id.saveRouteBtn);
        forgetRouteBtn = findViewById(R.id.forgetRouteBtn);
        createRouteBtn.setOnClickListener(createRouteBtnListener);
        saveRouteBtn.setOnClickListener(saveRouteBtnListener);
        forgetRouteBtn.setOnClickListener(forgetRouteBtnListener);

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        } else {
            Toast.makeText(this, "Вы не разрешили геолокацию!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);


        if (!filePath.exists()) {
            filePath.mkdir();
        }

        String selectedRoute = null;
        try {
            selectedRoute = getIntent().getExtras().getString("selectedRoute");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (selectedRoute != null) {
            drawExistingRoute(selectedRoute);
        }
    }

    @Override
    public void onMapLoaded() {
        coordinates = mMap.getMyLocation();
        currentPosition = new LatLng(coordinates.getLatitude(),coordinates.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,15));
    }

    //************************************* PORTALS ************************************//


    public void goToLoginScreen(View v) {
        Intent login = new Intent(MapsActivity.this,SignIn.class);
        startActivity(login);
    }

    public void goToRoutesList(View v) {
        Intent routesList = new Intent(MapsActivity.this,RoutesList.class);
        startActivity(routesList);
    }

    public void goToSettingsScreen(View v) {
        Intent settingsScreen = new Intent (MapsActivity.this,SettingsScreen.class);
        startActivity(settingsScreen);
    }
}
