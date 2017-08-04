package com.example.myapplication;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ServiceCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyService extends Service {
    String email;
    //private LocationManager locationManager;
    double curLat, curLon;
    String curTime;
    LocationManager locationManager;
    public MyService() {
    }

    final String LOG_TAG = "myLogs";

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
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

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            //tvLocationGPS.setText(formatLocation(location));
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            //tvLocationNet.setText(formatLocation(location));
        }
        curLat = location.getLatitude();
        curLon = location.getLongitude();
        //curTime = location.getTime();
        curTime = String.format(
                "Coordinates: lat = %1$.8f, lon = %2$.8f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
        int q = curTime.indexOf("time");
        StringBuffer sb = new StringBuffer(curTime);
        sb.delete(0, q + 7);
//        Toast toast = Toast.makeText(getApplicationContext(), sb, Toast.LENGTH_SHORT);
//        toast.show();
        sendCoord(curLat, curLon, email, sb.toString());
        // Toast.makeText(getApplicationContext(), "Координаты обновлены", Toast.LENGTH_SHORT);

    }

    private void checkEnabled() {
//        tvEnabledGPS.setText("Enabled: "
//                + locationManager
//                .isProviderEnabled(LocationManager.GPS_PROVIDER));
//        tvEnabledNet.setText("Enabled: "
//                + locationManager
//                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        email = intent.getStringExtra("email");

        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    void someTask() {
        new Thread(new Runnable() {
            public void run() {
                for (int i = 1; ; i++) {
                    Log.d(LOG_TAG, email);
                    Log.d(LOG_TAG, "i = " + i);
                    try {
                        TimeUnit.SECONDS.sleep(1);

                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                                1000 * 10, 10, locationListener);
                        Criteria criteria = new Criteria();
                        String provider = locationManager.getBestProvider(criteria, true);
                        // Getting Current Location
                        Location location = locationManager.getLastKnownLocation(provider);
                        locationListener.onLocationChanged(location);
                        if(location != null){
                            double d1 = location.getLatitude();
                            double d2 = location.getLongitude();
                            curTime = String.format(
                                    "Coordinates: lat = %1$.8f, lon = %2$.8f, time = %3$tF %3$tT",
                                    location.getLatitude(), location.getLongitude(), new Date(
                                            location.getTime()));
                            int q = curTime.indexOf("time");
                            StringBuffer sb = new StringBuffer(curTime);
                            sb.delete(0, q + 7);
                            Log.d(LOG_TAG, String.valueOf(d1));
                            Log.d(LOG_TAG, String.valueOf(d2));
                            sendCoord(d1, d2, email, sb.toString());
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //stopSelf();
            }
        }).start();

    }

    public void sendCoord(final double d1, final double d2, final String user, final String time){
        class Sending extends AsyncTask<String, Void, String> {
            String s1 = String.valueOf(d1);
            String s2 = String.valueOf(d2);
            private String mEmail = user;
            // private final String mPassword = password;

            @Override
            protected String doInBackground(String... params)  {
                Response response = null;

                try {
                    OkHttpClient client = new OkHttpClient();

                    FormBody.Builder formBuilder = new FormBody.Builder()
                            .add("login", mEmail)
                            .add("lat", s1)
                            .add("lon", s2)
                            .add("time", time)
                            ;
                    //Log.i("userrr", mEmail);
                    //Log.i("passs", mPassword);
                    RequestBody formBody = formBuilder.build();
                    Request request = new Request.Builder()
                            .url("http://sakhipych.esy.es/update.php")
                            .post(formBody)
                            .build();

                    response = client.newCall(request).execute();
                    // progress.show();
                    //Log.i("userrr", mEmail);
                    //Log.i("passs", mPassword);
                    return response.body().string();
                    // Log.i("resultik", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String s = null;
                try {
                    s = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                // progress.dismiss();
                if(s.trim().equals("success")){
                    Toast.makeText(getApplicationContext(), "Your coordinates are updated", Toast.LENGTH_SHORT);
                    Log.d(LOG_TAG, "updated");
                }

            }

            @Override
            protected void onCancelled() {
                // mAuthTask = null;
                //showProgress(false);
            }
        }
        Sending send = new Sending();
        send.execute();
    }

}
