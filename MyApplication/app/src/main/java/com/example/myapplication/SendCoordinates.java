package com.example.myapplication;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Date;

public class SendCoordinates extends Service {
    private LocationManager locationManager;
    Double lat, lon;
    String curTime;
    String LOG_TAG = "logtag";
    public SendCoordinates() {
        CoordinatesUpdater coordinatesUpdater = new CoordinatesUpdater(this);
        //coordinatesUpdater.getLocation();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
