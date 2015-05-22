package com.example.sixinwen.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.example.sixinwen.NewsShow;
import com.example.sixinwen.R;
import com.example.sixinwen.adapter.FirstPageNewsAdapter;
import com.example.sixinwen.utils.NewsItem;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by wangrunhui on 3/18/15.
 */
public class FirstPageHome extends Fragment{
    private ListView mListView;
    private FirstPageNewsAdapter mAdapter;
    private List<NewsItem> newsItemList;
    private List<AVObject> newslist;
    private Runnable updateNewsRunnable;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_page_home, container,
                false);
    }
    @Override
    public void onStart() {
        super.onStart();

        init();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),NewsShow.class);
                Bundle bundle = new Bundle();
                Log.d("WRH", "bundle put " + newslist.get(position));
                bundle.putString("NewsIndex", newslist.get(position).getObjectId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    void init() {
        mListView = (ListView)getActivity().findViewById(R.id.first_page_listview);
        newsItemList = new ArrayList<>();
        final ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.win10tx);
        mAdapter = new FirstPageNewsAdapter(getActivity(), newsItemList);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        mAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
        AVQuery<AVObject> query = new AVQuery<>("News");
        AVObject news = new AVObject("News");
        String title,content;
        newslist = new ArrayList<>();
        //query.whereEqualTo("playerName", "steve");
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                    int size = avObjects.size();
                    for (int i = 1; i < size; i++) {
                        AVObject obj = avObjects.get(i);
                        boolean show = obj.getBoolean("Now");
                        if (show){
                            newslist.add(obj);
                        }
                    }
                    new Thread(updateNewsRunnable).start();
                } else {
                    Log.d("失败", "查询错误: " + e.getMessage());
                }
            }
        });
        mListView.setAdapter(mAdapter);
        updateNewsRunnable = new Runnable() {
            @Override
            public void run() {
                int size = newslist.size();

                for (int i = 0; i < size; i++) {
                    AVObject obj= newslist.get(i);
                    //newslist.add(obj);
                    //Log.d("WRHH", "bundle put " + obj.get("objectId").getClass());
                    double support = obj.getDouble("SupportRatio");
                    //AVObject avFile = obj.getAVObject("Picture");
                    //avFile.getDataInBackground(datacallback);
                    Drawable drawable = null;
                    try {
                        String source = obj.getAVFile("Picture").getUrl();
                        URL url = new URL(source);
                        InputStream is = url.openStream();
                        //Log.d("打开URL成功", "");
                        drawable = Drawable.createFromStream(is, "");  //获取网路图片
                    } catch (Exception e) {
                        Log.d("获取网络图片失败", "获取网络图片查询错误: " + e.getMessage());
                    }

                    ImageView iw =  new ImageView(getActivity());
                    iw.setImageDrawable(drawable);
                    NewsItem newsItem = new NewsItem(obj.getString("Title"),
                            obj.getString("Content"),
                            support,
                            1-support,
                            iw,
                            obj.getInt("CommentNum"));
                    newsItemList.add(newsItem);
                }
                //mAdapter.notifyDataSetChanged();
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        };
/*      //获得单个新闻的用法如下
        query.getInBackground("552e8498e4b036ba524410ea", new GetCallback<AVObject>() {
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

 /*       try {
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
 */

    }

}
