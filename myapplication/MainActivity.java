package com.example.myapplication;

/**
 * Created by Илья on 13.06.2017.
 */

import java.io.IOException;
import java.util.Date;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ProgressDialog progress;
    String strr = "";
    String myJSON;
    TextView tvEnabledGPS;
    TextView tvStatusGPS;
    TextView tvLocationGPS;
    TextView tvEnabledNet;
    TextView tvStatusNet;
    TextView tvLocationNet;
    TextView showFrie;
    Button getFrie;
    Button showMap;
    Button btnSearch;
    private LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();
    String email, password;
    double curLon;
    double curLat;
    String curTime;
    Button btnRequest;
    private String arrayFriends[] = new String[10000];
    private double arrayLat[] = new double[10000];
    private double arrayLon[] = new double[10000];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent intent = getIntent();
        email = intent.getStringExtra("username");
        setTitle(email);
        password = intent.getStringExtra("password");
        btnSearch = (Button)findViewById(R.id.button4);
        btnRequest = (Button)findViewById(R.id.button5);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RequestActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);
        showFrie = (TextView) findViewById(R.id.showFrie);
        getFrie = (Button) findViewById(R.id.getFrie);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getFrie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getFriends(email);
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        showMap = (Button) findViewById(R.id.showMap);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("arrayFriends", arrayFriends);
                intent.putExtra("arrayLat", arrayLat);
                intent.putExtra("arrayLon", arrayLon);
                startActivity(intent);
            }
        });
        getFriends(email);
    }

        @Override
        protected void onResume() {
            super.onResume();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);
            checkEnabled();
        }

        @Override
        protected void onPause() {
            super.onPause();
            locationManager.removeUpdates(locationListener);
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
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
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
        sb.delete(0, q+7);
//        Toast toast = Toast.makeText(getApplicationContext(), sb, Toast.LENGTH_SHORT);
//        toast.show();
        sendCoord(curLat, curLon, email, sb.toString());
       // Toast.makeText(getApplicationContext(), "Координаты обновлены", Toast.LENGTH_SHORT);

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
        tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }
    protected void showList(){
        int l=0;
        try {
            l=0;
            if(myJSON.contains("{")) {
                JSONObject jsonObj = new JSONObject(myJSON.substring(myJSON.indexOf("{"), myJSON.lastIndexOf("}") + 1));
                l = 1;
                JSONArray p = jsonObj.getJSONArray("friends");
                l = 2;
                String s1, s2;
                int j = 0;
                for (int i = 0; i < p.length(); i++) {

                    JSONObject c = p.getJSONObject(i);
                    l = 3;
                    String name1 = c.getString("friend_name");
                    String lastname1 = c.getString("friend_lastname");
                    l = 4;
                    String lat1 = c.getString("lat");
                    l = 5;
                    String lon1 = c.getString("lon");
                    String time1 = c.getString("time");
                    arrayFriends[j] = name1 + " " + lastname1;
                    arrayLat[j] = Double.valueOf(lat1);
                    arrayLon[j] = Double.valueOf(lon1);
                    j++;
                    l = 6;
                    String str = name1 + " " + lastname1 + " " + lat1 + " " + lon1 + " " + time1 + "\n";


                    s1 = strr + str;
                    strr = s1;
                    Log.i("LOG_TAG", "str: " + str + " strr: " + strr);
                }

                showFrie.setText("");
                Toast toast = Toast.makeText(getApplicationContext(), "Список друзей обновлён", Toast.LENGTH_SHORT);
                toast.show();
                strr = "";

            }
            else  showFrie.setText("Нету данных в базе");
        } catch (JSONException e) {
            Log.i("LOG_TAG",e.toString()+" "+l);
        }

    }

    public void getFriends(final String email){
        class UserLoginTask extends AsyncTask<String, Void, String> {

            private final String mEmail = email;
           // private final String mPassword = password;

            @Override
            protected String doInBackground(String... params)  {
                Response response = null;

                try {
                    OkHttpClient client = new OkHttpClient();

                    FormBody.Builder formBuilder = new FormBody.Builder()
                            .add("username", mEmail);
                    //Log.i("userrr", mEmail);
                    //Log.i("passs", mPassword);
                    RequestBody formBody = formBuilder.build();
                    Request request = new Request.Builder()
                            .url("http://sakhipych.esy.es/friends.php")
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
                //String s = null;
                try {
                    myJSON = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return myJSON;
            }

            @Override
            protected void onPostExecute(String s) {
               // progress.dismiss();
                myJSON = s.trim();
                showList();
                // mAuthTask = null;
                //showProgress(false);
                /*if (s.trim().equals("success")) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("username", email);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    finish();
                } else {

                }*/
            }

            @Override
            protected void onCancelled() {
                // mAuthTask = null;
                //showProgress(false);
            }
        }
        UserLoginTask userLoginTask = new UserLoginTask();
        userLoginTask.execute();
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
    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };
    public void onClickStart(View v) {
        startService(new Intent(this, MyService.class).putExtra("email", email));
    }
    public void onClickStop(View v) {
        stopService(new Intent(this, MyService.class));
    }
}