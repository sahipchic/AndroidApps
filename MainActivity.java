package com.example.hackathonvk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView tv1, tv2;
    String myJSON;
    int times[] = new int[401];
    int people[] = new int[401];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);
        getData();
    }

    private void getData() {
        class dataTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                Response response = null;

                try {
                    OkHttpClient client = new OkHttpClient();

                    FormBody.Builder formBuilder = new FormBody.Builder()
                            .add("null", "null");
                    RequestBody formBody = formBuilder.build();
                    Request request = new Request.Builder()
                            .url("https://telegrambotdrozd.000webhostapp.com/data.php")
                            .post(formBody)
                            .build();

                    response = client.newCall(request).execute();
                    return response.body().string();

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
                if (!s.trim().equals("{\"rooms\":[]}") && !s.trim().equals(null)) {
                    myJSON = s.trim();
                    parseList();
                    //tv1.setText("Data");
                } else {
                    tv1.setText("No Data");
                }
            }

            @Override
            protected void onCancelled() {

            }
        }
        dataTask DataTask = new dataTask();
        DataTask.execute();
    }

    private void parseList() {
        try {
            if (myJSON.contains("{")) {
                JSONObject jsonObj = new JSONObject(myJSON.substring(myJSON.indexOf("{"), myJSON.lastIndexOf("}") + 1));
                JSONArray p = jsonObj.getJSONArray("rooms");
                String s = "";
                for (int i = 0; i < p.length(); i++) {
                    JSONObject c = p.getJSONObject(i);
                    String time = c.getString("time");
                    String n = c.getString("people");
                    times[i] = Integer.parseInt(time);
                    people[i] = Integer.parseInt(n);
                    s += time;
                    s += ' ';
                    s += n;
                    s += ' ';
                }
                tv1.setText(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

