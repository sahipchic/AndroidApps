package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.util.Date;

/**
 * Created by Илья on 25.08.2017.
 */

public class CoordinatesUpdater {
    private LocationManager locationManager;
    Double curLon, curLat;
    String curTime;
    CoordinatesUpdater(Context context)
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        checkEnabled();
    }

    void sgd()
    {
        locationManager.removeUpdates(locationListener);
    }
    public LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            getLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            getLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                //tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                //tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }
    };

    public Double[] getLocation(Location location) {
        if (location == null)
            return null;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            formatLocation(location);
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            formatLocation(location);
        }
        curLat = location.getLatitude();
        curLon = location.getLongitude();
        Double[] coord = new Double[]{
                curLat, curLon
        };
        return coord;
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";

        return String.format(
                "Coordinates: lat = %1$.8f, lon = %2$.8f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void checkEnabled() {

    }
}
