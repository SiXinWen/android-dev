package com.example.sixinwen;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

//import com.avoscloud.leanchatlib.controller.ChatManager;
import com.example.sixinwen.adapter.ChatMsgViewAdapter;
import com.example.sixinwen.utils.ChatMsgEntity;

import org.json.JSONException;
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
    final int ConversationType_OneOne = 0;
    final int ConversationType_Group = 1;
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

    private ImageView mShowTitle;
    private TextView mNewsTitle;
    private LinearLayout mBloodBar;
    private String newsDetailString = "";
    private TextView mNewsDetail;
    private TextView mSupportLine;
    private TextView mOpposeLine;
    //判断是否隐藏新闻详细信息
    private boolean hideText = true;
    private OnClickListener mTitleClick = new OnClickListener() {
        public void onClick (View v) {
            switch(v.getId()) {

                case R.id.news_show_title:
                case R.id.et_news_title:
                case R.id.bloodbar:
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
    private AVIMConversation mAvimConversation;
    private AVIMClient mImClient;
    private AVIMClientCallback avimClientCallback;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.news_show);
        //AVOSCloud.setDebugLogEnabled(true);
        initView();
        avimClientCallback = new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                Log.d("Logout", "log out successfully");
            }
        };

        Bundle bundle = getIntent().getExtras();
        indexOfNews = bundle.getInt("NewsIndex");
        initData();
        //AVAnalytics.trackAppOpened(getIntent());
        AVQuery<AVObject> query = new AVQuery<>("News");
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    //Log.d("newsShow成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                    obj = avObjects.get(indexOfNews + 1);
                    mNewsTitle.setText(obj.getString("Title"));
                    double support = obj.getDouble("SupportRatio");
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
                    lp1.weight = (float)support;
                    mSupportLine.setLayoutParams(lp1);
                    lp2.weight = (float)(1-support);
                    mOpposeLine.setLayoutParams(lp2);
 /*                   new Thread(new Runnable() {
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
 */                 new myAsyncTask().execute();

                } else {
                    Log.d("失败", "查询错误: " + e.getMessage());
                }
            }
        });
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mImClient.close(avimClientCallback);
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
        mNewsTitle.setOnClickListener(mTitleClick);
        mBloodBar.setOnClickListener(mTitleClick);
        loginChat();
    }
    private void initView() {
        mLeftSend = (Button)findViewById(R.id.btn_send_left);
        mRightSend = (Button)findViewById(R.id.btn_send_right);
        mEditText = (EditText)findViewById(R.id.et_sendmessage);
        mListView = (ListView)findViewById(R.id.chat_msg_listview);
        mBack = (Button)findViewById((R.id.news_show_back));

        mShowTitle = (ImageView)findViewById(R.id.et_news_title);
        mNewsTitle = (TextView)findViewById(R.id.news_show_title);
        mBloodBar = (LinearLayout)findViewById(R.id.bloodbar);
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
    private void leftSend() {
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
    private void rightSend() {
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
        mImClient.close(avimClientCallback);
        finish();
        return true;
    }

    private class myAsyncTask extends  AsyncTask<String,String,Spanned> {

        @Override
        protected Spanned doInBackground(String... params) {
            Log.d("AsyncTask","in doInBackground");
            Spanned spanned = Html.fromHtml(obj.getString("htmlContent"), new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    Drawable drawable;
                    URL url;
                    try {
                        url = new URL(source);
                        //Log.d("打开URL成功", ""+url);
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
            }, null);
            //Log.d("WRH","in doInBackground after html, spanned = "+spanned);
            return spanned;
        }

        @Override
        protected void onPostExecute(Spanned result)
        {
            //Log.d("WRH","in onPostExecute, result = "+result);
            //更新UI的操作，这里面的内容是在UI线程里面执行的
            mNewsDetail.setText(result);
        }
    }


    private void loginChat() {
        Log.d("iniChat", "begin");
        mImClient = AVIMClient.getInstance("share");
        mImClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVException e) {
                if (null != e) {
                    // 出错了，可能是网络问题无法连接 LeanCloud 云端，请检查网络之后重试。
                    // 此时聊天服务不可用。
                    e.printStackTrace();
                } else {
                    // 成功登录，可以开始进行聊天了（假设为 MainActivity）。
                    //Intent intent = new Intent(currentActivity, MainActivity.class);
                    //currentActivity.startActivity(intent);
                    Log.d("iniChat", "ok");
                    joinGroupChat();
                }
            }
        });
        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());
    }
    class CustomMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            // 新消息到来了。在这里增加你自己的处理代码。
            String msgContent = message.getContent();
            Log.d("onMessage",conversation.getConversationId() + " 收到一条新消息：" + msgContent);
        }
    }
    private void joinGroupChat() {
        //final ChatManager chatManager = ChatManager.getInstance();


        //mAvimConversation = new AVIMConversation(mImClient,"5545ca24e4b03ccbae7046a6");
        AVObject avObject = obj.getAVObject("conv");
        String newsConvId = avObject.getString("objectId");
        AVIMConversationQuery conversationQuery = mImClient.getQuery();
//        AVIMConversation conversation = mImClient.getConversation(newsConvId);
//        Log.d("JoinGroupChat", conversation + "");
        //conversationQuery.containsMembers(clients);

        // 之前有常量定义：
        // const int ConversationType_OneOne = 0;
        // const int ConversationType_Group = 1;
        //conversationQuery.whereEqualTo("attr.type", ConversationType_Group);
        conversationQuery.findInBackground(new AVIMConversationQueryCallback(){
            @Override
            public void done(List<AVIMConversation> conversations, AVException e) {
                Log.d("find done","");
                if (null != e) {
                    // 出错了。。。
                    e.printStackTrace();
                } else {
                    if (null != conversations) {
                        AVObject avObject1 = obj.getAVObject("conv");
                        String newsConvId = avObject1.getObjectId();
                        Log.d("after find Convs", "  newConvID=" + newsConvId + "conv:"+avObject1);
                        int size = conversations.size();
                        for (int i = 0; i < size; i++) {
                            AVIMConversation avimConversation = conversations.get(i);
                            String convId = avimConversation.getConversationId();
                            Log.d("after find Convs", "convID=" + convId + "  newConvID=" + newsConvId);
                            if (convId.equals(newsConvId)) {
                                //find the conversation
                                Log.d("search room", "find it");
                                mAvimConversation = avimConversation;
                                mAvimConversation.join(new AVIMConversationCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        Log.d("join", "done!");
                                    }
                                });
                                break;
                            }
                        }
                        Log.d("find conversation", "找到了符合条件的 " + conversations.size() + " 个对话");
                    } else {
                        Log.d("fail to find conversati","没有找到符合条件的对话");
                    }
                }
            }
        });
    }

}
