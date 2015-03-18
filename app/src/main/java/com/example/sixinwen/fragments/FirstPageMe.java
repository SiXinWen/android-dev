package com.example.sixinwen.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.sixinwen.R;

/**
 * Created by kakarotto on 3/18/15.
 */
public class FirstPageMe extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View newsLayout = inflater.inflate(R.layout.first_page_me, container,
                false);
        return newsLayout;
    }
    @Override
    public void onStart() {
        super.onStart();
        init();
    }
    void init() {
    }
}
