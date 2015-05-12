package com.example.sixinwen;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.example.sixinwen.adapter.ChatMsgViewAdapter;
import com.example.sixinwen.utils.ChatMsgEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVAnalytics;

import static android.view.View.OnClickListener;

/**
 * Created by kakarotto on 3/26/15.
 */
public class NewsShow extends Activity {
    private Button mLeftSend;
    private Button mRightSend;
    private Button mBack;
    private EditText mEditText;
    private ChatMsgViewAdapter mChatMsgViewAdapter;
    private ListView mListView;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();

    private TextView mNewsTitle;
    private String newsDetailString = new String("4月25日尼泊尔里氏8.1级地震发生以后，由于" +
            "尼泊尔是一个旅游国家，大量外国游客滞留尼境内。有消息显示，震后中国飞机第一个到尼" +
            "泊尔，接回中国游客。有不少人叫好的同时，也出现不同声音，有人发微博：让中国人先走" +
            "！尼泊尔撤侨又见大国沙文主义。不少网友跟评，“看见祖国这么‘流氓’，我就放心了！”" +
            "4月25日尼泊尔里氏8.1级地震发生以后，由于尼泊尔是一个旅游国家，大量外国游客滞留尼" +
            "境内。有消息显示，震后中国飞机第一个到尼泊尔，接回中国游客。有不少人叫好的同时，" +
            "也出现不同声音，有人发微博：让中国人先走！尼泊尔撤侨又见大国沙文主义。不少网友跟评，" +
            "“看见祖国这么‘流氓’，我就放心了！”4月25日尼泊尔里氏8.1级地震发生以后，由于尼" +
            "泊尔是一个旅游国家，大量外国游客滞留尼境内。有消息显示，震后中国飞机第一个到尼泊" +
            "尔，接回中国游客。有不少人叫好的同时" +
            "，也出现不同声音，有人发微博：让中国人先走！尼泊尔撤侨又见大国沙" +
            "文主义。不少网友跟评，“看见祖国这么‘流氓’，我就放心了！”");
    private TextView mNewsDetail;
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
        initData();
        //AVAnalytics.trackAppOpened(getIntent());
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

        mNewsTitle.setOnClickListener(mTitleClick);

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

        mNewsTitle = (TextView)findViewById(R.id.et_news_title);
        mNewsDetail = (TextView)findViewById(R.id.et_news_detail);
        mNewsDetail.setMovementMethod(ScrollingMovementMethod.getInstance());


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
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);
        return sbBuffer.toString();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return true;
    }

}
