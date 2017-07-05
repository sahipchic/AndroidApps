package com.example.cppproject;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Илья on 04.07.2017.
 */

public class WebViewManager {
    private ArrayList<WebView> tabs;
    private ArrayList<Integer> curTabs;
    private SortedSet<Integer> freeTabs;
    private int current = 0;
    private final int N = 5;
    private int currentSize = 0;
    private LinearLayout contaner;
    private Context context;
    private final String startPage = "http://www.ya.ru";

    public WebViewManager(Context context, LinearLayout linearLayout) {
        contaner = linearLayout;
        this.context = context;
        tabs = new ArrayList<>();
        freeTabs = new TreeSet<>();
        curTabs = new ArrayList<>();
        createNewPull();
        addNewWebView();
        getCurrent().setVisibility(View.VISIBLE);

    }


    public WebView getCurrent() {
        return tabs.get(current);
    }

    public int getCurrentIndex() {
        return curTabs.indexOf(current);
    }

    public int getIndexWebView(WebView w) {
        return curTabs.indexOf(tabs.indexOf(w));
    }

    public void setCurrent(int index) {
        if (index < getSize()) {
            if (current != curTabs.get(index)) {
                getCurrent().setVisibility(View.GONE);
                current = curTabs.get(index);
                getCurrent().setVisibility(View.VISIBLE);
            }

        }
    }

    public int getSize() {
        return currentSize;
    }

    public WebView addNewWebView() {

        WebView webView = tabs.get(freeTabs.first());
        curTabs.add(freeTabs.first());
        freeTabs.remove(freeTabs.first());
        currentSize++;
        contaner.addView(webView);
        if(currentSize >= tabs.size() - 1) {
            createNewPull();
        }
        return webView;
    }

    public void deleteWebView(int index) {
        int ind = curTabs.get(index);
        freeTabs.add(ind);
        getCurrent().setVisibility(View.GONE);
        getCurrent().loadUrl(startPage);
        curTabs.remove(index);

        currentSize--;
        contaner.removeAllViews();
        if (getSize() == 0) {
            addNewWebView();
        } else {
            for (int i = 0; i < getSize(); ++i) {
                contaner.addView(tabs.get(curTabs.get(i)));
            }
        }
        current = curTabs.get(0);
        getCurrent().setVisibility(View.VISIBLE);
    }

    private void createNewPull() {
        for (int i = 0; i < N; i++) {
            WebView webView = new WebView(context);
            webView.setWebViewClient(new MyWebViewClient());
            webView.setVisibility(View.GONE);
            webView.loadUrl(startPage);
            webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            tabs.add(webView);
            freeTabs.add(tabs.size() - 1);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


}