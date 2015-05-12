package com.example.sixinwen.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
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
    private List<AVObject> newslist;
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
        AVOSCloud.initialize(getActivity(), "epg58oo2271uuupna7b9awz9nzpcxes870uj0j0rzeqkm8mh", "xjgx65z5yavhg8nj4r48004prjelkq0fzz9xgricyb2nh0qq");

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
        final ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.win10tx);
        mAdapter = new FirstPageNewsAdapter(getActivity(), newsItemList);
        AVQuery<AVObject> query = new AVQuery<AVObject>("News");
        AVObject news = new AVObject("News");
        String title,content;
        newslist = new ArrayList<AVObject>();
        //query.whereEqualTo("playerName", "steve");
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                    int size = avObjects.size();
                    for (int i = 1; i < size; i++) {
                        AVObject obj= avObjects.get(i);
                        newslist.add(obj);
                        double support = obj.getDouble("SupportRatio");
                        NewsItem newsItem = new NewsItem(obj.getString("Title"), obj.getString("Content"), support, 1-support, imageView);
                        // have to get picture here!
                        newsItemList.add(newsItem);
                    }
                    mAdapter.notifyDataSetChanged();

                } else {
                    Log.d("失败", "查询错误: " + e.getMessage());
                }
            }
        });
/*        query.getInBackground("552e8498e4b036ba524410ea", new GetCallback<AVObject>() {
            public void done(AVObject inews, AVException e) {
                if (e == null) {
                    Log.d("WRH", "Title：" + inews.getString("Title"));
                    //news = "hehe";
                    newslist.add(inews);
                    NewsItem newsItem = new NewsItem(inews.getString("Title"),inews.getString("Content"),4,3,imageView);
                    newsItemList.add(newsItem);
                    newsItemList.add(newsItem);
                    newsItemList.add(newsItem);
                    newsItemList.add(newsItem);
                    newsItemList.add(newsItem);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.e("WRH", "错误: " + e.getMessage());
                }
            }
        });
 */
        //query.getQuery("TestObject");

        try {
            news = query.get("552e8498e4b036ba524410ea");
            //Log.d("WRH","hehe"+query.get("552e8498e4b036ba524410ea"));
            Log.d("WRH","hehehehe");
            //news[1] = query.get("552e841de4b036ba52440bbb");
            //news[2] = query.get("552e841de4b036ba52440bbb");
            //news[3] = query.get("552e841de4b036ba52440bbb");
            //news[4] = query.get("552e841de4b036ba52440bbb");
        } catch (AVException e) {
            e.getMessage();Log.d("WRH","hehehehe"+e.getMessage());
        }
        //System.out.println("query="+query);
        Log.d("WRH", "news=" + news);
        NewsItem newsItem = new NewsItem(news.getString("Title"),news.getString("Content"),4,3,imageView);
        //NewsItem newsItem = new NewsItem("testKey","testKey",4,3,imageView);
        //newsItemList.add(newsItem);
        //newsItemList.add(newsItem);
        //newsItemList.add(newsItem);
        //newsItemList.add(newsItem);
        //newsItemList.add(newsItem);
        mListView.setAdapter(mAdapter);
    }


}
