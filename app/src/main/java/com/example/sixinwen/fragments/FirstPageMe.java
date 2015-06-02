package com.example.sixinwen.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import com.example.sixinwen.R;
import com.example.sixinwen.AboutUs;

/**
 * Created by kakarotto on 3/18/15.
 */
public class FirstPageMe extends Fragment {
    private TableRow mMeAboutUs = null;

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
        mMeAboutUs = (TableRow) getActivity().findViewById(R.id.me_about_us);
        mMeAboutUs.setOnClickListener(MeAboutUsListener);
    }

    private View.OnClickListener MeAboutUsListener = new View.OnClickListener() {
        public void onClick(View v) {
            //getActivity().setContentView(R.layout.about_us);
            Intent intent = new Intent(getActivity(), AboutUs.class);
            startActivity(intent);
        }
    };

}

