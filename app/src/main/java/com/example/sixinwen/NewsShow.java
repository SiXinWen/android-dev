package com.example.sixinwen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private Button mLeftSend;
    private Button mRightSend;
    private ImageButton mBack;
    private Button mShare;
    private EditText mEditText;
    private ChatMsgViewAdapter mChatMsgViewAdapter;
    private ChatMsgViewAdapter mHotChatMsgViewAdapter;
    private ListView mListView;
    private ListView mHotListView;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<>();
    private List<ChatMsgEntity> mHotArrays = new ArrayList<>();
    private List<String> mCommentIds = new ArrayList<>();
    private List<String> mHotCommentIds = new ArrayList<>();
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
    private LinearLayout input;
    private LinearLayout mClickBox;
    private TextView mSupport;
    private TextView mDefute;
    private int mClickPosition;
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
        AVOSCloud.setDebugLogEnabled(true);
        initWechatShare();
        mChatMsgViewAdapter = new ChatMsgViewAdapter(NewsShow.this, mDataArrays);
        mHotChatMsgViewAdapter = new ChatMsgViewAdapter(NewsShow.this, mHotArrays);
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
        queryConversation();

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
        mLeftSend.setClickable(false);
        mRightSend.setClickable(false);
        mShowTitle.setOnClickListener(mTitleClick);
        mNewsTitle.setOnClickListener(mTitleClick);
        mBloodBar.setOnClickListener(mTitleClick);
        loginChat();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mClickBox.setVisibility(View.VISIBLE);
                mClickPosition = position;
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
                //builder.create().show();
            }
        });
        mHotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mClickBox.setVisibility(View.VISIBLE);
                mClickPosition = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(NewsShow.this);
                builder.setMessage("赞 or 踩");
                builder.setTitle("你的态度");
                builder.setPositiveButton("赞", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //message的commentId，找comment，找用户
                        String commentId = mHotCommentIds.get(position);
                        supportComment(commentId);
                    }
                });
                builder.setNegativeButton("踩", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String commentId = mHotCommentIds.get(position);
                        dislikeComment(commentId);
                    }
                });
                builder.create().show();
            }
        });
        mInstaComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setVisibility(View.VISIBLE);
                mHotListView.setVisibility(View.GONE);
                input.setVisibility(View.VISIBLE);

            }
        });
        mHotComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setVisibility(View.GONE);
                mHotListView.setVisibility(View.VISIBLE);
                input.setVisibility(View.GONE);
            }
        });
        //initHotComments();
        mShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                wechatShare(0);
            }
        });
    }

    private void queryConversation() {
        AVQuery<AVObject> query = new AVQuery<>("News");
        query.whereEqualTo("objectId", indexOfNews);
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    //Log.d("newsShow成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                    obj = avObjects.get(0);
                    mNewsTitle.setText(obj.getString("Title"));

                    double support = obj.getDouble("SupportNum");
                    double refute = obj.getDouble("RefuteNum");
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp1.weight = (float) support;
                    mSupportLine.setLayoutParams(lp1);
                    mSupportLine.setText(obj.getString("AffirmativeView"));
                    lp2.weight = (float) refute;
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
                    new myAsyncTask().execute();// get news content (images) from leancloud

                } else {
                    Log.d("失败", "查询错误: " + e.getMessage());
                }
            }
        });
    }
    private void initView() {
        mLeftSend = (Button)findViewById(R.id.btn_send_left);
        mRightSend = (Button)findViewById(R.id.btn_send_right);
        mEditText = (EditText)findViewById(R.id.et_sendmessage);
        mListView = (ListView)findViewById(R.id.chat_msg_listview);
        mHotListView = (ListView)findViewById(R.id.chat_msg_hotlistview);
        mBack = (ImageButton)findViewById((R.id.news_show_back));
        mShare = (Button)findViewById((R.id.news_show_share));

        mInstaComment = (TextView) findViewById(R.id.news_show_instant_comment);
        mHotComment = (TextView) findViewById(R.id.news_show_hot_comment);

        mShowTitle = (ImageView)findViewById(R.id.et_news_title);
        mNewsTitle = (TextView)findViewById(R.id.news_show_title);
        mBloodBar = (LinearLayout)findViewById(R.id.bloodbar);
        mNewsDetail = (TextView)findViewById(R.id.et_news_detail);
        mNewsDetail.setMovementMethod(ScrollingMovementMethod.getInstance());
        mSupportLine = (TextView) findViewById(R.id.news_show_support);
        mOpposeLine = (TextView) findViewById(R.id.news_show_oppose);
        input = (LinearLayout) findViewById(R.id.rl_bottom);

        mClickBox = (LinearLayout) findViewById(R.id.click_box);
        mClickBox.setVisibility(View.GONE);
        mSupport = (TextView) findViewById(R.id.support_click);
        mSupport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickBox.setVisibility(View.GONE);
                String commentId = mCommentIds.get(mClickPosition);
                supportComment(commentId);
            }
        });
        mDefute = (TextView) findViewById(R.id.defute_click);
        mDefute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickBox.setVisibility(View.GONE);
                String commentId = mCommentIds.get(mClickPosition);
                dislikeComment(commentId);
            }
        });
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

            String comment = ((AVIMTextMessage) message).getText();
            Log.d("onMessage", comment);
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
            mListView.setSelection(mDataArrays.size() - 1);
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
                Log.d("find done","\n");
                if (null != e) {
                    // 出错了。。。
                    Log.d("conversation error", e.getMessage());

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

                Toast.makeText(NewsShow.this, "可以进行评论啦！", Toast.LENGTH_SHORT);
                if (list == null) {
                    return;
                } else {
                    mRightSend.setClickable(true);
                    mLeftSend.setClickable(true);
                    int size = list.size();
                    Log.d("initData", "messagelist.size = " + size);
                    for (int i = 0; i < size; i++) {
                        AVIMTextMessage avimTextMessage = (AVIMTextMessage) list.get(i);
                        Map<String, Object> attr = avimTextMessage.getAttrs();
                        Boolean attitude = (Boolean) attr.get("attitude");
                        String commentId = (String) attr.get("commentId");
                        if (commentId == null) commentId = "...";
                        //Log.d("initData", "commentId = " + commentId+"size = "+size);
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
        initHotComments();
    }

    private void initHotComments() {
        AVQuery<AVObject> query = new AVQuery<>("HotComments");
        //AVObject news = new AVObject("News");
        //String title,content;
        query.whereEqualTo("TargetConv", targetConvId);
        Log.d("hot comments", "开始,targetConvId = " + targetConvId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {

                if (list == null) {
                    return;
                } else {
                    Log.d("hot comments", "size = " + list.size());
                    int size = list.size();
                    //Log.d("initHotComment", "list.size = " + size);
                    for (int i = 0; i < size; i++) {
                        AVObject hotComment = list.get(i);
                        final String commentString = hotComment.getString("Comments");
                        Log.d("hot commetnId",commentString);
                        AVQuery<AVObject> query1 = new AVQuery<>("Comments");
                        query1.whereEqualTo("objectId", commentString);
                        query1.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                Log.d("hot comment query", "" + list.size());
                                if (list == null) {
                                    return;
                                } else {
                                    AVObject comment = list.get(0);
                                    String commentId = comment.getObjectId();
                                    if (commentId == null) commentId = "...";
                                    mHotCommentIds.add(commentId);
                                    Log.d("hotData1", "commentId = " + commentId + "size = " + list.size());
                                    String content = comment.getString("Content");
                                    int like = comment.getInt("Like");
                                    int dislike = comment.getInt("Dislike");

                                    Boolean attitude = (Boolean) comment.get("Attitude");
                                    ChatMsgEntity entity = new ChatMsgEntity();

                                    if (attitude.equals(Boolean.TRUE)) {
                                        entity.setDate("" + comment.getString("createdAt"));
                                        entity.setName("支持方");
                                        entity.setText(content);
                                        entity.setMsgType(true);

                                    } else {
                                        entity.setDate("" + comment.getString("createdAt"));
                                        entity.setName("反对方");
                                        entity.setText(content);
                                        entity.setMsgType(false);
                                    }
                                    Log.d("hot", "hotarrays.size = " + mHotArrays.size());
                                    mHotArrays.add(entity);
                                }
                            }
                        });

                    }
                }

                mHotListView.setAdapter(mHotChatMsgViewAdapter);
                mHotListView.setSelection(mHotArrays.size() - 1);
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
            attr.put("attitude", Boolean.FALSE);
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
            attr.put("attitude", Boolean.TRUE);
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

    //wechat share code
    private IWXAPI wxApi;
//实例化
    private void initWechatShare() {

    wxApi = WXAPIFactory.createWXAPI(this, "wxe4aa47f0f98d1d04",true);
    wxApi.registerApp("wxe4aa47f0f98d1d04");
    }
    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     * @param flag(0:分享到微信好友,1:分享到微信朋友圈)
     */
    private void wechatShare(int flag){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://sixinwen.avosapps.com/share?nid=5573e468e4b03c3d0281e5ae";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "对于 复旦教授与崔永元就转基因展开激辩 我认为...";
        msg.description = "日前，崔永元在复旦大学交流会上与教授就食品转基因问题展开辩论，教授一开场便不愿回答小崔的问题，直斥对方没资格跟自己讨论黄金大米的科学性。当问到黄金大米究竟转入了几个基因时，教授竟不知该如何回答...更多";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }
    /**在需要分享的地方添加代码：
        wechatShare(0);//分享到微信好友
        wechatShare(1);//分享到微信朋友圈
    */
}
