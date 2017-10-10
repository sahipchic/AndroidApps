package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Илья on 02.09.2017.
 */

public class RegistrationActivity extends Activity {
    AutoCompleteTextView tvName;
    AutoCompleteTextView tvSurname;
    EditText tvLogin;
    EditText tvPass;
    EditText tvCheck;
    TextView tvError;
    String s1, s2, s3, s4, s5, myJSON;
    Button btnReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        Intent intent = getIntent();
        tvName = (AutoCompleteTextView) findViewById(R.id.tvName);
        tvSurname = (AutoCompleteTextView) findViewById(R.id.tvSurname);
        tvLogin = (EditText) findViewById(R.id.tvLogin);
        tvPass = (EditText) findViewById(R.id.tvPass);
        tvCheck = (EditText) findViewById(R.id.tvCheck);
        tvError = (TextView) findViewById(R.id.tvError);
        btnReg = (Button) findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    private void register(){
        s1 = tvName.getText().toString().trim();
        s2 = tvSurname.getText().toString().trim();
        s3 = tvLogin.getText().toString().trim();
        s4 = tvPass.getText().toString().trim();
        s5 = tvCheck.getText().toString().trim();
        if(s1 != "" && s2 != "" && s3 != "" && s4 != "" && s5 != ""){
            if(!s4.equals(s5)){
                tvPass.setText("");
                tvCheck.setText("");
                tvError.setText("Пароли не совпадают");
            }
            else{
                if(s1.length() < 3 || s2.length() < 3 || s3.length() < 3 || s4.length() < 3 || s5.length() < 3){
                    tvError.setText("В каждом поле не менее 3-х символов");
                }
                else {
                    tvError.setText("");
                    sendData();
                }
            }
        }
        else{
            tvError.setText("Вы заполнили не все поля");
        }
    }
    private void sendData(){
        final String name = tvName.getText().toString();
        final String lastname = tvSurname.getText().toString();
        final String login = tvLogin.getText().toString();
        final String pass = tvPass.getText().toString();
        class UserLoginTask extends AsyncTask<String, Void, String> {

            private final String mName = name;
            private final String mLastname = lastname;
            private final String mLogin= login;
            private final String mPass = pass;

            @Override
            protected String doInBackground(String... params)  {
                Response response = null;

                try {
                    OkHttpClient client = new OkHttpClient();

                    FormBody.Builder formBuilder = new FormBody.Builder()
                            .add("name", mName)
                            .add("lastname", mLastname)
                            .add("email", mLogin)
                            .add("pass", mPass);
                    Log.i("userrr", mName);
                    Log.i("passs", mLastname);
                    RequestBody formBody = formBuilder.build();
                    Request request = new Request.Builder()
                            .url("http://sakhipych.esy.es/index2.php")
                            .post(formBody)
                            .build();

                    response = client.newCall(request).execute();
                    Log.i("userrr", mName);
                    Log.i("passs", mLastname);

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

                if (s.trim().equals("reged")) {
                    myJSON = s.trim();
                    tvError.setText("Данный логин уже занят другим пользователем");
                    tvPass.setText("");
                    tvCheck.setText("");
                } else if(s.trim().equals("success")) {
                    tvName.setVisibility(View.GONE);
                    tvSurname.setVisibility(View.GONE);
                    tvLogin.setVisibility(View.GONE);
                    tvPass.setVisibility(View.GONE);
                    tvCheck.setVisibility(View.GONE);
                    btnReg.setVisibility(View.GONE);
                    tvError.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    tvError.setText("Регистрация прошла успешно!\n Ваш логин: " + tvLogin.getText().toString() + "\n Ваш пароль: " + tvPass.getText().toString() + "\nНажмите кнопку Назад, чтобы войти в свою учетную запись.");
                }
                else {
                    tvPass.setText("");
                    tvCheck.setText("");
                    tvError.setText("Неизвестная ошибка");
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

}
