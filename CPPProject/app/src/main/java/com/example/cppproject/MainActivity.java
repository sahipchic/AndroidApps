package com.example.cppproject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class MainActivity extends Activity {

    private WebViewManager tabs;
    private ArrayList<String> tabsName;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView searchText;
    private Spinner spinner;
    private Button btn1, btn2, btn3;
    String text, myJSON;
    String[] ms = new String[10];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        init();
        webSetting();
        //updateCurrentWeb();
    }
    public void check(final Button btn){
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String st = searchText.getText().toString();
                if (btn.getText().toString().length() != 0) {
                    String url = btn.getText().toString();
                    if (!url.contains(".")) {
                        searchText.setText(url);
                        url = "https://yandex.ru/search/?text=" + url;
                    } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = String.format("http://%s", url);
                    }
                    tabs.getCurrent().loadUrl(url);
                }
                else return;
            }
        });
    }
    private void init(){
        tabs = new WebViewManager(this, (LinearLayout) findViewById(R.id.container));

        searchText = (AutoCompleteTextView) findViewById(R.id.autoComplete);

        searchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_ENTER) {
                    goUrl();
                    return true;
                }
                return false;
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String t = s.toString();
                if(t.trim() != "" && t.trim() != null)
                    getFriends(t);
                else{
                    btn1.setVisibility(View.INVISIBLE);
                    btn2.setVisibility(View.INVISIBLE);
                    btn3.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });
        spinner = (Spinner) findViewById(R.id.spinner);

        tabsName = new ArrayList<>();
        tabsName.add(tabs.getCurrent().getUrl());

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tabsName);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                tabs.setCurrent(selectedItemPosition);
                //searchText.setText(tabsName.get(selectedItemPosition));
                searchText.setText("");
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



    }

    private void updateCurrentWeb() {
        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedHint = (String) adapterView.getItemAtPosition(position);
                    String urlSearch = "https://yandex.ru/search/?text=" + selectedHint;
                    tabs.getCurrent().loadUrl(urlSearch);

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                searchText.setText(selectedHint);
            }
        });
    }


    public void Go(View view) {
        goUrl();
    }

    private void goUrl(){
        String u = searchText.getText().toString();
        if(!u.startsWith("http://") && !u.startsWith("https://")) {
            if(!u.contains(".")) {
                u = "https://yandex.ru/search/?text=" + u;
            } else {
                u = "http://" + u;
            }
        }
        tabs.getCurrent().loadUrl(u);
    }

    @Override
    public void onBackPressed() {
        if (tabs.getCurrent().canGoBack()) {
            tabs.getCurrent().goBack();
        } else {
            super.onBackPressed();
        }
    }
    public String findcov(String text){

        String pattern = "\"[^\"]+\"";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        StringBuilder s = new StringBuilder();
        int q = 0;
        if(m.find())
            s.append(text.substring(m.start()+1, m.end()-1));

        while(m.find() && q < 9) {
            String str = text.substring(m.start()+1, m.end()-1);
            //ms[q] = text.substring(m.start()+1, m.end()-1);
            if(str.length() < 30){
                ms[q] = str;
                q++;
            }
            s.append(text.substring(m.start()+1, m.end()-1));
            s.append(' ');
        }
        return s.toString();


    }
    public void getFriends(final String t){
        class UserLoginTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params)  {
                Response response = null;
                text = t;
                try {
                    OkHttpClient client = new OkHttpClient();

                    FormBody.Builder formBuilder = new FormBody.Builder()
                            .add("text", text);

                    RequestBody formBody = formBuilder.build();
                    Request request = new Request.Builder()
                            .url("http://suggest.yandex.ru/suggest-ff.cgi?part=" + text)
                            .post(formBody)
                            .build();

                    response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    myJSON = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return myJSON;
            }

            @Override
            protected void onPostExecute(String s) {
                myJSON = s.trim();
                String s1 = (findcov(myJSON));
                if(myJSON != null)
                {
                    btn1.setVisibility(View.VISIBLE);
                    btn1.setText(ms[0]);
                    btn2.setVisibility(View.VISIBLE);
                    btn2.setText(ms[1]);
                    btn3.setVisibility(View.VISIBLE);
                    btn3.setText(ms[2]);
                    check(btn1);
                    check(btn2);
                    check(btn3);
                }
            }

            @Override
            protected void onCancelled() {

            }
        }
        UserLoginTask userLoginTask = new UserLoginTask();
        userLoginTask.execute();
    }
    public void addWeb(View view) {
        WebView w = tabs.addNewWebView();
        tabs.setCurrent(tabs.getIndexWebView(w));
        tabsName.add(tabs.getCurrent().getUrl());
        adapter.notifyDataSetChanged();
        spinner.setSelection(tabs.getCurrentIndex());
        webSetting();

    }

    private void webSetting(){
        WebSettings settings = tabs.getCurrent().getSettings();
        tabs.getCurrent().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        settings.setDomStorageEnabled(true);
        tabs.getCurrent().setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //searchText.setText(url);
                tabsName.set(tabs.getCurrentIndex(), url);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void delWeb(View view) {
        tabsName.remove(tabs.getCurrentIndex());
        if(tabsName.isEmpty()) {
            tabsName.add("Yandex");
        }
        tabs.deleteWebView(tabs.getCurrentIndex());
        adapter.notifyDataSetChanged();
        spinner.setSelection(tabs.getCurrentIndex());

    }
    public void Clear(View view){
        searchText.setText("");
    }
    public native String stringFromJNI();
    public native String foo(String json);
    public native String fo(String json, int q);
    public native String yeah(String s);
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}

//

