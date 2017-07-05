package com.example.cppproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import android.widget.Filter;
import android.widget.Filterable;

//import static com.example.cppproject.MainActivity.getFriends;
//import static com.example.cppproject.MainActivity.myJSON;


public class AdapterForEdit extends ArrayAdapter<String> implements Filterable {


    private ArrayList<String> list;

    public AdapterForEdit(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int index) {
        return list.get(index);
    }


    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                   // getFriends(constraint.toString());
                    //String[] s  = MainActivity.findcov(myJSON);/*Сюда*/;
                    String[] s = new String[2];
                    list = new ArrayList<>(Arrays.asList(s));

                    filterResults.values = list;
                    filterResults.count = list.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence cont, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

        };

    }

}