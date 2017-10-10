package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by Илья on 15.06.2017.
 */

public class SearchActivity extends AppCompatActivity {
    String email, friend, name, lastname;
    Button btn2, btn3;
    EditText editText2, editText3;
    TextView text;
    String myJSON;
    LinearLayout linlay;
    LinearLayout panel, ppanel;
    int j = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        Intent intent = getIntent();
        linlay = (LinearLayout)findViewById(R.id.linlay);
        email = intent.getStringExtra("email");
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        text = (TextView)findViewById(R.id.textView);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(friend);
            }
        });
    }
    private void find(){
        final String name = editText2.getText().toString();
        final String lastname = editText3.getText().toString();
        class UserLoginTask extends AsyncTask<String, Void, String> {

            private final String mName = name;
            private final String mLastname = lastname;

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
                            .add("name", mName)
                            .add("lastname", mLastname);
                    Log.i("userrr", mName);
                    Log.i("passs", mLastname);
                    RequestBody formBody = formBuilder.build();
                    Request request = new Request.Builder()
                            .url("http://sakhipych.esy.es/find_user.php")
                            .post(formBody)
                            .build();

                    response = client.newCall(request).execute();
                    Log.i("userrr", mName);
                    Log.i("passs", mLastname);
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
                if (!s.trim().equals("{\"friends\":[]}") && !s.trim().equals(null)) {
                    myJSON = s.trim();
                    //text.setText(myJSON);
                    text.setText("");
                    showList();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("username", email);
//                    intent.putExtra("password", password);
//                    startActivity(intent);
//                    finish();
                } else {
                    if(j > 0){
                        linlay.removeView(ppanel);
                        //j--;
                    }
                    text.setText("Нет такого пользователя");
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
    private void add(final String friend){
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
                            .url("http://sakhipych.esy.es/adding.php")
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

                    Toast toast = Toast.makeText(getApplicationContext(), "Заявка отправлена", Toast.LENGTH_SHORT);
                    toast.show();
                } else if(s.trim().equals("added")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Вы уже добавили данного пользователя", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(s.trim().equals("sent")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Вы уже послали заявку данному пользователю", Toast.LENGTH_SHORT);
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
    protected void showList(){
        if(j > 0){
            linlay.removeView(ppanel);
        }
        j = 0;
        int l=0;
        try {
            l=0;
            if(myJSON.contains("{")) {
                JSONObject jsonObj = new JSONObject(myJSON.substring(myJSON.indexOf("{"), myJSON.lastIndexOf("}") + 1));
                l = 1;
                JSONArray p = jsonObj.getJSONArray("friends");
                l = 2;
                String s1, s2;
                ppanel = new LinearLayout(SearchActivity.this);

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
                    j++;
                    l = 6;
                    String str = name1 + " " + lastname1 + " " + login1 + "\n";

                    panel = new LinearLayout(SearchActivity.this);

                    panel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    panel.setOrientation(LinearLayout.HORIZONTAL);
                    final TextView tw = new TextView(SearchActivity.this);
                    final Button btn = new Button(SearchActivity.this);
                    tw.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT, 0.5f));
                    btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                    tw.setText(str);
                    btn.setText("Добавить");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            add(login1);
                        }
                    });
                    panel.addView(tw);
                    panel.addView(btn);
                    ppanel.addView(panel);

                }
                linlay.addView(ppanel);

            }
            else  text.setText("Нет пользователя с такими параметрами");
        } catch (JSONException e) {
            Log.i("LOG_TAG",e.toString()+" "+l);
        }

    }
}
