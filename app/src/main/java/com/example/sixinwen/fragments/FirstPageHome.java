package com.example.sixinwen.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.sixinwen.R;

/**
 * Created by kakarotto on 3/18/15.
 */
public class FirstPageHome extends Fragment{
    private ListView mListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View newsLayout = inflater.inflate(R.layout.first_page_home, container,
                false);
        return newsLayout;
    }
    @Override
    public void onStart() {
        super.onStart();
        init();
    }
    void init() {
        mListView = (ListView)getActivity().findViewById(R.id.first_page_listview);
    }
}
