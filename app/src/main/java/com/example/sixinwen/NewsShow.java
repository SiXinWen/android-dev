package com.example.sixinwen;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.sixinwen.adapter.ChatMsgViewAdapter;
import com.example.sixinwen.utils.ChatMsgEntity;
import com.example.sixinwen.utils.NewsItem;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.OnClickListener;

/**
 * Created by wangrunhui on 3/26/15.
 */
public class NewsShow extends Activity {
    private Button mLeftSend;
    private Button mRightSend;
    private Button mBack;
    private EditText mEditText;
    private ChatMsgViewAdapter mChatMsgViewAdapter;
    private ListView mListView;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<>();
    private AVObject news;
    private int indexOfNews;
    private AVObject obj;

    private TextView mShowTitle;
    private TextView mNewsTitle;
    private String newsDetailString = "";
    private TextView mNewsDetail;
    private TextView mSupportLine;
    private TextView mOpposeLine;
    //判断是否隐藏新闻详细信息
    private boolean hideText = true;
    private OnClickListener mTitleClick = new OnClickListener() {
        public void onClick (View v) {
            switch(v.getId()) {
                case R.id.et_news_title:
                    if(hideText) {
                        mNewsDetail.setVisibility(View.VISIBLE);
                        hideText = false;
                    } else {
                        mNewsDetail.setVisibility(View.GONE);
                        hideText = true;
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        //AVOSCloud.initialize(this, "epg58oo2271uuupna7b9awz9nzpcxes870uj0j0rzeqkm8mh", "xjgx65z5yavhg8nj4r48004prjelkq0fzz9xgricyb2nh0qq");
        setContentView(R.layout.news_show);
        initView();
        Bundle bundle = getIntent().getExtras();
        indexOfNews = bundle.getInt("NewsIndex");Log.d("WRHH", "" + indexOfNews);
        initData();
        //AVAnalytics.trackAppOpened(getIntent());
        AVQuery<AVObject> query = new AVQuery<>("News");
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                    obj = avObjects.get(indexOfNews + 1);
                    mNewsTitle.setText(obj.getString("Title"));
                    double support = obj.getDouble("SupportRatio");
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
                    lp1.weight = (float)support;
                    mSupportLine.setLayoutParams(lp1);
                    lp2.weight = (float)(1-support);
                    mOpposeLine.setLayoutParams(lp2);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mNewsDetail.setText(Html.fromHtml(obj.getString("htmlContent"), new Html.ImageGetter() {
                                @Override
                                public Drawable getDrawable(String source) {
                                    Drawable drawable;
                                    URL url;
                                    try {
                                        url = new URL(source);//Log.d("打开URL成功", ""+url.openStream());
                                        InputStream is = url.openStream();
                                        drawable = Drawable.createFromStream(is, "");  //获取网路图片
                                    } catch (Exception e) {
                                        Log.d("获取网络图片失败", "获取网络图片查询错误: " + e.getMessage());
                                        return null;
                                    }
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                                            .getIntrinsicHeight());

                                    return drawable;
                                }
                            }, null));
                        }
                    }).start();

                } else {
                    Log.d("失败", "查询错误: " + e.getMessage());
                }
            }
        });
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRightSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                leftSend();
            }
        });
        mLeftSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rightSend();
            }
        });

        mShowTitle.setOnClickListener(mTitleClick);

        AVObject testObject = new AVObject("TestObject");
        //testObject.put("foo", "hehe");
        //testObject.saveInBackground();
    }
    private void initView() {
        mLeftSend = (Button)findViewById(R.id.btn_send_left);
        mRightSend = (Button)findViewById(R.id.btn_send_right);
        mEditText = (EditText)findViewById(R.id.et_sendmessage);
        mListView = (ListView)findViewById(R.id.chat_msg_listview);
        mBack = (Button)findViewById((R.id.news_show_back));

        mShowTitle = (TextView)findViewById(R.id.et_news_title);
        mNewsTitle = (TextView)findViewById(R.id.news_show_title);
        mNewsDetail = (TextView)findViewById(R.id.et_news_detail);
        mNewsDetail.setMovementMethod(ScrollingMovementMethod.getInstance());
        mSupportLine = (TextView) findViewById(R.id.news_show_support);
        mOpposeLine = (TextView) findViewById(R.id.news_show_oppose);

    }
    private String[] msgArray = new String[]{"  孩子们，要好好学习，天天向上！要好好听课，不要翘课！不要挂科，多拿奖学金！三等奖学金的争取拿二等，二等的争取拿一等，一等的争取拿励志！",
            "姚妈妈还有什么吩咐...",
            "还有，明天早上记得跑操啊，不来的就扣德育分！",
            "德育分是什么？扣了会怎么样？",
            "德育分会影响奖学金评比，严重的话，会影响毕业",
            "哇！学院那么不人道？",
            "你要是你不听话，我当场让你不能毕业！",
            "姚妈妈，我知错了(- -我错在哪了...)"};

    private String[]dataArray = new String[]{"2012-09-01 18:00", "2012-09-01 18:10",
            "2012-09-01 18:11", "2012-09-01 18:20",
            "2012-09-01 18:30", "2012-09-01 18:35",
            "2012-09-01 18:40", "2012-09-01 18:50"};
    private final static int COUNT = 8;
    private void initData() {

        mNewsDetail.setText(newsDetailString);

        for(int i = 0; i < COUNT; i++) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(dataArray[i]);
            if (i % 2 == 0)
            {
                entity.setName("姚妈妈");
                entity.setMsgType(true);
            }else{
                entity.setName("Shamoo");
                entity.setMsgType(false);
            }

            entity.setText(msgArray[i]);
            mDataArrays.add(entity);
        }
        mChatMsgViewAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mChatMsgViewAdapter);
    }
    private void leftSend()
    {
        String contString = mEditText.getText().toString();
        if (contString.length() > 0)
        {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(getDate());
            entity.setName("");
            entity.setMsgType(false);
            entity.setText(contString);
            mDataArrays.add(entity);
            mChatMsgViewAdapter.notifyDataSetChanged();
            mEditText.setText("");
            mListView.setSelection(mListView.getCount() - 1);
            mChatMsgViewAdapter.notifyDataSetChanged();//NotifyDataSetChanged();
            mListView.setSelection(mDataArrays.size()-1);
        }
    }
    private void rightSend()
    {
        String contString = mEditText.getText().toString();
        if (contString.length() > 0)
        {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(getDate());
            entity.setName("");
            entity.setMsgType(true);
            entity.setText(contString);
            mDataArrays.add(entity);
            mChatMsgViewAdapter.notifyDataSetChanged();
            mEditText.setText("");
            mListView.setSelection(mListView.getCount() - 1);
            mChatMsgViewAdapter.notifyDataSetChanged();
            mListView.setSelection(mDataArrays.size()-1);
        }
    }

    //获取日期
    private String getDate() {
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));
        StringBuilder sbBuffer = new StringBuilder();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);
        return sbBuffer.toString();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return true;
    }

}
