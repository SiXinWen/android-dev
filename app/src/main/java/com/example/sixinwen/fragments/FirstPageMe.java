package com.example.sixinwen.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.sixinwen.Login;
import com.example.sixinwen.MyApplication;
import com.example.sixinwen.R;
import com.example.sixinwen.AboutUs;
import com.example.sixinwen.Register;

/**
 * Created by kakarotto on 3/18/15.
 */
public class FirstPageMe extends Fragment {
    private TableRow mMeAboutUs = null;
    private TableRow mUser;
    private TextView name;
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
        mUser = (TableRow) getActivity().findViewById(R.id.more_page_row0);
        mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });
        name = (TextView) getActivity().findViewById(R.id.name);
        name.setText(MyApplication.getUsername());
    }

    private View.OnClickListener MeAboutUsListener = new View.OnClickListener() {
        public void onClick(View v) {
            //getActivity().setContentView(R.layout.about_us);
            Intent intent = new Intent(getActivity(), AboutUs.class);
            startActivity(intent);
        }
    };

}

