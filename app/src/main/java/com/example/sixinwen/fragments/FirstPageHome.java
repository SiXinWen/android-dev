package com.example.sixinwen.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.sixinwen.NewsShow;
import com.example.sixinwen.R;
import com.example.sixinwen.adapter.FirstPageNewsAdapter;
import com.example.sixinwen.utils.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kakarotto on 3/18/15.
 */
public class FirstPageHome extends Fragment{
    private ListView mListView;
    private FirstPageNewsAdapter mAdapter;
    private List<NewsItem> newsItemList;
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),NewsShow.class);
                startActivity(intent);
            }
        });
    }
    void init() {
        mListView = (ListView)getActivity().findViewById(R.id.first_page_listview);
        newsItemList = new ArrayList<NewsItem>();
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.win10tx);
        mAdapter = new FirstPageNewsAdapter(getActivity(), newsItemList);
        NewsItem newsItem = new NewsItem("微软腾讯战略合作","微软腾讯战略合作 电脑管家可一键升级Win10",4,3,imageView);
        newsItemList.add(newsItem);
        newsItemList.add(newsItem);
        newsItemList.add(newsItem);
        newsItemList.add(newsItem);
        newsItemList.add(newsItem);
        mListView.setAdapter(mAdapter);
    }


}
