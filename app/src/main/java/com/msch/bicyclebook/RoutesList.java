package com.msch.bicyclebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;

public class RoutesList extends AppCompatActivity {

    public static final String TAG = "RoutesList ";
    private ArrayList<String> mRV_names = new ArrayList<>();
    private ArrayList<Float> mRV_distances = new ArrayList<>();
    private ArrayList<String> mRV_times = new ArrayList<>();
    private ArrayList<int[]> mRV_colors = new ArrayList<int[]>();
    public ArrayList<String> routesIDs = new ArrayList<>();


    public void scanRoutesFiles() {
        File fileDir = new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/" + getPackageName() + "/savedRoutes/");
        File[] listOfFiles = fileDir.listFiles();
        try {
            for (int i = 0; i < listOfFiles.length; i++) {
                routesIDs.add(listOfFiles[i].getName());
            }
        } catch (EmptyStackException e) {
            e.printStackTrace();
        }
    }

    public String readRouteFile(String routeFileName) {
        String routeInfo = "";
        try {
            File routeFile = new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/" + getPackageName() + "/savedRoutes/" + routeFileName);
            FileInputStream inputStream = new FileInputStream(routeFile);

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

    public void fillRecycler(String routeFileName) {
        Route_info completeRouteInfo = new Route_info();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        completeRouteInfo = gson.fromJson(readRouteFile(routeFileName), Route_info.class);
        mRV_names.add(completeRouteInfo.getRouteName());
        mRV_distances.add(completeRouteInfo.getTraveledDistance());
        mRV_times.add(completeRouteInfo.getRouteTime());
        mRV_colors.add(completeRouteInfo.getRouteColor());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanRoutesFiles();
        for (int i = 0; i < routesIDs.size(); i++) {
            fillRecycler(routesIDs.get(i));
        }
        setContentView(R.layout.routeslist_container);
        Log.d(TAG, "onCreate: started.");
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.routeslist_RV);
        Button mRV_deleteBtn = findViewById(R.id.RV_deleteBtn);
        Button mRV_uploadBtn = findViewById(R.id.RV_uploadBtn);
        ImageView mRV_waypointIcon = findViewById(R.id.RV_image);
        RoutesList_Adapter adapter = new RoutesList_Adapter(mRV_names, mRV_distances, mRV_times, mRV_colors, mRV_deleteBtn, mRV_uploadBtn, mRV_waypointIcon, routesIDs ,this );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
