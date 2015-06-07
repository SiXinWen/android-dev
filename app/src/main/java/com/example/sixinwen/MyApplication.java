package com.example.sixinwen;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.example.sixinwen.adapter.FirstPageNewsAdapter;
import com.example.sixinwen.utils.NewsItem;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${Fastrun} on ${3/18/15}.
 */
public class MyApplication extends Application {
    private static List<NewsItem> newsItemList;
    private static List<AVObject> newslist;
    private static FirstPageNewsAdapter mAdapter;
    private static Runnable updateNewsRunnable;
    private static Handler mHandler;

    public static List<NewsItem> getNewsItemList() {
        return newsItemList;
    }

    public static List<AVObject> getNewslist() {
        return newslist;
    }

    public static FirstPageNewsAdapter getmAdapter() {
        return mAdapter;
    }

    public static Handler getmHandler() {
        return mHandler;
    }

    public void onCreate(){
        AVOSCloud.initialize(this, "epg58oo2271uuupna7b9awz9nzpcxes870uj0j0rzeqkm8mh", "xjgx65z5yavhg8nj4r48004prjelkq0fzz9xgricyb2nh0qq");
        //AVInstallation.getCurrentInstallation().saveInBackground();
        PushService.setDefaultPushCallback(this, SixinwenActivity.class);
        PushService.subscribe(this, "public", SixinwenActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    // 关联  installationId 到用户表等操作……
                    Log.d("installationId:", installationId);
                } else {
                    // 保存失败，输出错误信息
                    Log.d("install save failed：", e.getMessage());
                }
            }
        });
        init();
        newsItemList = new ArrayList<>();
        mAdapter = new FirstPageNewsAdapter(this, newsItemList);
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
        queryNews();
    }

    private void queryNews() {
        AVQuery<AVObject> query = new AVQuery<>("News");
        //AVObject news = new AVObject("News");
        //String title,content;
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
                        if (show) {
                            newslist.add(obj);
                        }
                    }
                    new Thread(updateNewsRunnable).start();
                } else {
                    Log.d("失败", "查询错误: " + e.getMessage());
                }
            }
        });
    }

    private void init() {
        updateNewsRunnable = new Runnable() {
            @Override
            public void run() {
                int size = newslist.size();

                for (int i = 0; i < size; i++) {
                    AVObject obj= newslist.get(i);
                    //newslist.add(obj);
                    //Log.d("WRHH", "bundle put " + obj.get("objectId").getClass());

                    double support = obj.getDouble("SupportNum");
                    double refute = obj.getDouble("RefuteNum");
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

                    ImageView iw =  new ImageView(MyApplication.this);
                    iw.setImageDrawable(drawable);
                    NewsItem newsItem = new NewsItem(obj.getString("Title"),
                            obj.getString("Content"),
                            support,
                            refute,
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
    }
}
