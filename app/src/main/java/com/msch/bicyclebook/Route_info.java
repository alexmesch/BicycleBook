package com.msch.bicyclebook;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Route_info {
    @SerializedName("routeId")
    private String routeId;

    @SerializedName("routeName")
    private String routeName;

    @SerializedName("traveledDistance")
    private float traveledDistance;

    @SerializedName("routeTime")
    private String routeTime;

    @SerializedName("routePoints")
    private ArrayList<LatLng> routePoints;

    @SerializedName("routeColor")
    private int[] routeColor;



    //public String getRouteId() { return routeId; }

    public String getRouteName() { return routeName; }

    public String getRouteTime() { return routeTime; }

    public float getTraveledDistance() { return traveledDistance; }

    public ArrayList getRoutePoints() { return routePoints; }

    public int[] getRouteColor() { return routeColor; }


    public void setRouteId(String routeId) { this.routeId = routeId; }

    public void setRouteName(String routeName) { this.routeName = routeName; }

    public void setRouteTime (String routeTime) { this.routeTime = routeTime; }

    public void setTraveledDistance (float traveledDistance) { this.traveledDistance = traveledDistance; }

    public void setRoutePoints (ArrayList<LatLng> routePoints) { this.routePoints = routePoints; }

    public void setRouteColor(int[] routeColor) { this.routeColor = routeColor; }
}
