package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Илья on 24.07.2017.
 */

public class RequestActivity extends AppCompatActivity {
    String email, myJSON;
    LinearLayout panel, ppanel, linlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        linlay = (LinearLayout)findViewById(R.id.linlay);
        getRequests();
    }
    private void getRequests(){
        class UserLoginTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params)  {
                Response response = null;
                // TODO: attempt authentication against a network service.
                //Intent intent = new Intent();
                //startActivity(intent);
                try {
                    OkHttpClient client = new OkHttpClient();

                    FormBody.Builder formBuilder = new FormBody.Builder()
                            .add("email", email);
                    RequestBody formBody = formBuilder.build();
                    Request request = new Request.Builder()
                            .url("http://sakhipych.esy.es/request.php")
                            .post(formBody)
                            .build();

                    response = client.newCall(request).execute();
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
                // mAuthTask = null;
                //showProgress(false);
                if (!s.trim().equals("{\"requests\":[]}") && !s.trim().equals(null)) {
                    myJSON = s.trim();
                    showList();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет входящих заявок", Toast.LENGTH_SHORT);
                    toast.show();
                }
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
    private void add_friend(final String friend, final Button btn){
        class UserLoginTask extends AsyncTask<String, Void, String> {

            private final String friend_login = friend;
            private final String Email = email;

            /*UserLoginTask(String email, String password) {
                mEmail = email;
                mPassword = password;
            }*/

            @Override
            protected String doInBackground(String... params)  {
                Response response = null;
                // TODO: attempt authentication against a network service.
                //Intent intent = new Intent();
                //startActivity(intent);
                try {
                    OkHttpClient client = new OkHttpClient();

                    FormBody.Builder formBuilder = new FormBody.Builder()
                            .add("username", Email)
                            .add("friend", friend_login);
//                    Log.i("userrr", mName);
//                    Log.i("passs", mLastname);
                    RequestBody formBody = formBuilder.build();
                    Request request = new Request.Builder()
                            .url("http://sakhipych.esy.es/add_friend.php")
                            .post(formBody)
                            .build();

                    response = client.newCall(request).execute();
//                    Log.i("userrr", mName);
//                    Log.i("passs", mLastname);
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
                // mAuthTask = null;
                //showProgress(false);
                if (s.trim().equals("success")) {
                    myJSON = s.trim();
                    btn.setVisibility(View.GONE);
                    Toast toast = Toast.makeText(getApplicationContext(), "Пользователь добавлен к вам в друзья", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT);
                    toast.show();
                }
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
    private void showList(){

        int l=0;
        try {
            l=0;
            if(myJSON.contains("{")) {
                JSONObject jsonObj = new JSONObject(myJSON.substring(myJSON.indexOf("{"), myJSON.lastIndexOf("}") + 1));
                l = 1;
                JSONArray p = jsonObj.getJSONArray("requests");
                l = 2;
                String s1, s2;
                ppanel = new LinearLayout(RequestActivity.this);

                ppanel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                ppanel.setOrientation(LinearLayout.VERTICAL);
                for (int i = 0; i < p.length(); i++) {

                    JSONObject c = p.getJSONObject(i);
                    l = 3;
                    String name1 = c.getString("name");
                    String lastname1 = c.getString("lastname");
                    l = 4;
                    final String login1 = c.getString("login");
                    //j++;
                    l = 6;
                    String str = name1 + " " + lastname1 + " " + login1 + "\n";

                    panel = new LinearLayout(RequestActivity.this);

                    panel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    panel.setOrientation(LinearLayout.HORIZONTAL);
                    final TextView tw = new TextView(RequestActivity.this);
                    final Button btn = new Button(RequestActivity.this);
                    tw.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT, 0.5f));
                    btn.setTextColor(Color.WHITE);
                    tw.setTextColor(Color.BLACK);
                    btn.setBackgroundColor(Color.BLACK);
                    btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                    tw.setText(str);
                    btn.setText("Принять");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            add_friend(login1, btn);
                        }
                    });
                    panel.addView(tw);
                    panel.addView(btn);
                    ppanel.addView(panel);

                }
                linlay.addView(ppanel);

            }

        } catch (JSONException e) {
            Log.i("LOG_TAG",e.toString()+" "+l);
        }
    }
}
