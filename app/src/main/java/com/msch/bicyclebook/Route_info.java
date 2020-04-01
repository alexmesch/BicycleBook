package com.msch.bicyclebook;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
//This is object for writing to JSON
public class Route_info {
    private int routeId;
    private String routeName;
    private long traveledDistance;
    private CharSequence routeTime;
    private ArrayList<LatLng> routePoints;

    //getters
    public int getRouteId() { return routeId; }

    public String getRouteName() { return routeName; }

    public CharSequence getRouteTime() { return routeTime; }

    public long getTraveledDistance() { return traveledDistance; }

    public ArrayList getRoutePoints() { return routePoints; }

    //setters
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public void setRouteName(String routeName) { this.routeName = routeName; }

    public void setRouteTime (CharSequence routeTime) { this.routeTime = routeTime; }

    public void setTraveledDistance (long traveledDistance) { this.traveledDistance = traveledDistance; }

    public void setRoutePoints (ArrayList<LatLng> routePoints) { this.routePoints = routePoints; }
}
