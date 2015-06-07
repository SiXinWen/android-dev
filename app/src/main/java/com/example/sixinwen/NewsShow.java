package com.example.sixinwen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

//import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.example.sixinwen.adapter.ChatMsgViewAdapter;
import com.example.sixinwen.utils.ChatMsgEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.view.View.OnClickListener;

/**
 * Created by wangrunhui on 3/26/15.
 */
public class NewsShow extends Activity {
    private static Context context;

    public static Context getContext() {
        return context;
    }
    private Button mLeftSend;
    private Button mRightSend;
    private ImageButton mBack;
    private EditText mEditText;
    private ChatMsgViewAdapter mChatMsgViewAdapter;
    private ListView mListView;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<>();
    private List<ChatMsgEntity> mHotArrays = new ArrayList<>();
    private List<String> mCommentIds = new ArrayList<>();
    private AVObject news;
    private String indexOfNews;
    private AVObject obj;

    private ImageView mShowTitle;
    private TextView mNewsTitle;
    private LinearLayout mBloodBar;
    private String targetConvId;
    private TextView mNewsDetail;
    private TextView mSupportLine;
    private TextView mOpposeLine;
    private TextView mInstaComment;
    private TextView mHotComment;
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
        mChatMsgViewAdapter = new ChatMsgViewAdapter(NewsShow.this, mDataArrays);
        initView();
        mLeftSend.setClickable(false);
        mRightSend.setClickable(false);
        avimClientCallback = new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                Log.d("Logout", "log out successfully");
            }
        };

        Bundle bundle = getIntent().getExtras();
        indexOfNews = bundle.getString("NewsIndex");
        //AVAnalytics.trackAppOpened(getIntent());
        AVQuery<AVObject> query = new AVQuery<>("News");
        query.whereEqualTo("objectId", indexOfNews);
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    //Log.d("newsShow成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                    obj = avObjects.get(0);
                    mNewsTitle.setText(obj.getString("Title"));
                    double support = obj.getDouble("SupportRatio");
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp1.weight = (float) support;
                    mSupportLine.setLayoutParams(lp1);
                    mSupportLine.setText(obj.getString("AffirmativeView"));
                    lp2.weight = (float) (1 - support);
                    mOpposeLine.setLayoutParams(lp2);
                    mOpposeLine.setText(obj.getString("OpposeView"));
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
 */
                    new myAsyncTask().execute();

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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewsShow.this);
                builder.setMessage("赞 or 踩");
                builder.setTitle("你的态度");
                builder.setPositiveButton("赞", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //message的commentId，找comment，找用户
                        String commentId = mCommentIds.get(position);
                        supportComment(commentId);
                    }
                });
                builder.setNegativeButton("踩", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String commentId = mCommentIds.get(position);
                        dislikeComment(commentId);
                    }
                });
                builder.create().show();
            }
        });
        mInstaComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mHotComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void initView() {
        mLeftSend = (Button)findViewById(R.id.btn_send_left);
        mRightSend = (Button)findViewById(R.id.btn_send_right);
        mEditText = (EditText)findViewById(R.id.et_sendmessage);
        mListView = (ListView)findViewById(R.id.chat_msg_listview);
        mBack = (ImageButton)findViewById((R.id.news_show_back));

        mInstaComment = (TextView) findViewById(R.id.news_show_instant_comment);
        mHotComment = (TextView) findViewById(R.id.news_show_hot_comment);

        mShowTitle = (ImageView)findViewById(R.id.et_news_title);
        mNewsTitle = (TextView)findViewById(R.id.news_show_title);
        mBloodBar = (LinearLayout)findViewById(R.id.bloodbar);
        mNewsDetail = (TextView)findViewById(R.id.et_news_detail);
        mNewsDetail.setMovementMethod(ScrollingMovementMethod.getInstance());
        mSupportLine = (TextView) findViewById(R.id.news_show_support);
        mOpposeLine = (TextView) findViewById(R.id.news_show_oppose);

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
                    Log.d("正在打开URL", "");
                    try {
                        url = new URL(source);
                        Log.d("打开URL成功", ""+url);
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
            Log.d("WRH","in doInBackground after html, spanned = "+spanned);
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
        //注意！！AVIMMessageManager.registerDefaultMessageHandler() 一定要在 AVIMClient.open() 之前调用，否则可能导致服务器发回来的部分消息丢失。
        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());
        mImClient = AVIMClient.getInstance("wangrunhui");
        mImClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVException e) {
                if (null != e) {
                    // 出错了，可能是网络问题无法连接 LeanCloud 云端，请检查网络之后重试。
                    // 此时聊天服务不可用。
                    Log.d("iniChat", "error!!");
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

    }
    class CustomMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            // 新消息到来了。在这里增加你自己的处理代码。
            Map<String, Object> attr = ((AVIMTextMessage)message).getAttrs();
            Boolean attitude = (Boolean) attr.get("attitude");
            String commentId = (String) attr.get("commentId");
            mCommentIds.add(commentId);
            String comment = message.getContent();
            ChatMsgEntity entity = new ChatMsgEntity();
            if (attitude.equals(Boolean.TRUE)) {
                entity.setDate("" + message.getTimestamp());
                entity.setText(comment);
                entity.setMsgType(true);
                entity.setName("支持方");
            } else {
                entity.setDate("" + message.getTimestamp());
                entity.setText(comment);
                entity.setMsgType(false);
                entity.setName("反对方");
            }
            mDataArrays.add(entity);
            mChatMsgViewAdapter.notifyDataSetChanged();
            String msgContent = message.getContent();
            Log.d("onMessage",conversation.getConversationId() + " 收到一条新消息：" + msgContent);
        }
    }
    private void joinGroupChat() {
        //final ChatManager chatManager = ChatManager.getInstance();


        //mAvimConversation = new AVIMConversation(mImClient,"5545ca24e4b03ccbae7046a6");
        AVObject avObject = obj.getAVObject("conv");
        String newsConvId = avObject.getObjectId();
        AVIMConversationQuery conversationQuery = mImClient.getQuery();
//        AVIMConversation conversation = mImClient.getConversation(newsConvId);
//        Log.d("JoinGroupChat", conversation + "");
        //conversationQuery.containsMembers(clients);

        // 之前有常量定义：
        // const int ConversationType_OneOne = 0;
        // const int ConversationType_Group = 1;
        //conversationQuery.whereEqualTo("attr.type", ConversationType_Group);
        conversationQuery.whereEqualTo("objectId",newsConvId);
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
                        //Log.d("after find Convs", "  newConvID=" + newsConvId + "conv:"+avObject1);int size = conversations.size();
                        AVIMConversation avimConversation = conversations.get(0);
                        String convId = avimConversation.getConversationId();
                        targetConvId = convId;
                        Log.d("after find Convs", "convID=" + convId + "  newConvID=" + newsConvId);
                        if (convId.equals(newsConvId)) {
                            //find the conversation
                            Log.d("search room", "find it");
                            mAvimConversation = avimConversation;
                            mAvimConversation.join(new AVIMConversationCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        Log.d("join", "done!");
                                        initData();

                                    }
                                });
                        }

                        Log.d("find conversation", "找到了符合条件的 " + conversations.size() + " 个对话");
                    } else {
                        Log.d("fail to find conversati","没有找到符合条件的对话");
                    }
                }
            }
        });
    }

    private void initData() {
        //mNewsDetail.setText(newsDetailString);
        Log.d("search history", "begin");
        mAvimConversation.queryMessages(20, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVException e) {
                Log.d("get history", "find messagae list");
                mRightSend.setClickable(true);
                mLeftSend.setClickable(true);
                Toast.makeText(NewsShow.this, "可以进行评论啦！", Toast.LENGTH_SHORT);
                if (list == null) {
                    return;
                } else {
                    int size = list.size();
                    Log.d("initData", "messagelist.size = " + size);
                    for (int i = 0; i < size; i++) {
                        AVIMTextMessage avimTextMessage = (AVIMTextMessage) list.get(i);
                        Map<String, Object> attr = avimTextMessage.getAttrs();
                        Boolean attitude = (Boolean) attr.get("attitude");
                        String commentId = (String) attr.get("commentId");
                        if (commentId == null) commentId = "...";
                        Log.d("initData", "commentId = " + commentId+"size = "+size);
                        mCommentIds.add(commentId);
                        String comment = avimTextMessage.getContent();
                        try {
                            JSONObject jsonObject = new JSONObject(avimTextMessage.getContent());
                            comment = jsonObject.getString("_lctext");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        ChatMsgEntity entity = new ChatMsgEntity();
                        if (attitude.equals(Boolean.TRUE)) {
                            entity.setDate("" + avimTextMessage.getTimestamp());
                            entity.setText(comment);
                            entity.setMsgType(true);
                            entity.setName("支持方");
                        } else {
                            entity.setDate("" + avimTextMessage.getTimestamp());
                            entity.setText(comment);
                            entity.setMsgType(false);
                            entity.setName("反对方");
                        }
                        mDataArrays.add(entity);
                    }
                }
                mListView.setAdapter(mChatMsgViewAdapter);
                mListView.setSelection(mDataArrays.size() - 1);
            }
        });

    }

    private void initHostComments() {
        AVQuery<AVObject> query = new AVQuery<>("Comments");
        //AVObject news = new AVObject("News");
        //String title,content;
        query.whereEqualTo("objectId", targetConvId);
        Log.d("赞", "开始,commentId = " + targetConvId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {

            }
        });
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
            mListView.setSelection(mDataArrays.size() - 1);
            AVIMTextMessage avimTextMessage = new AVIMTextMessage();
            Map<String, Object> attr = new Hashtable<>();
            attr.put("attitude", Boolean.TRUE);
            avimTextMessage.setText(contString);
            avimTextMessage.setAttrs(attr);
            mAvimConversation.sendMessage(avimTextMessage, new AVIMConversationCallback() {
                @Override
                public void done(AVException e) {
                    Log.d("Send message", "Left done");
                }
            });
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
            AVIMTextMessage avimTextMessage = new AVIMTextMessage();
            Map<String, Object> attr = new Hashtable<>();
            attr.put("attitude", Boolean.FALSE);
            avimTextMessage.setText(contString);
            avimTextMessage.setAttrs(attr);
            mAvimConversation.sendMessage(avimTextMessage, new AVIMConversationCallback() {
                @Override
                public void done(AVException e) {
                    Log.d("Send message", "Right done");
                }
            });
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
    private void supportComment(String commentId) {
        AVQuery<AVObject> query = new AVQuery<>("Comments");
        //AVObject news = new AVObject("News");
        //String title,content;

        query.whereEqualTo("objectId", commentId);
        Log.d("赞", "开始,commentId = " + commentId);
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的评论");
                    int size = avObjects.size();
                    if (size == 0) {
                        return;
                    }
                    avObjects.get(0).increment("Like");
                    avObjects.get(0).increment("heat");
                    avObjects.get(0).saveInBackground();
                    Log.d("成功", "增加赞");
                } else {
                    Log.d("失败", "查询错误: " + e.getMessage());
                }
            }
        });
    }
    private void dislikeComment(String commentId) {
        AVQuery<AVObject> query = new AVQuery<>("Comments");
        //AVObject news = new AVObject("News");
        //String title,content;

        query.whereEqualTo("objectId", commentId);
        Log.d("赞", "开始,commentId = " + commentId);
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的评论");
                    int size = avObjects.size();
                    if (size == 0) {
                        return;
                    }
                    avObjects.get(0).increment("Dislike");
                    avObjects.get(0).increment("heat");
                    avObjects.get(0).saveInBackground();
                    Log.d("成功", "增加赞");
                } else {
                    Log.d("失败", "查询错误: " + e.getMessage());
                }
            }
        });
    }
}
