package com.example.sixinwen.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.avos.avoscloud.AVObject;
import com.example.sixinwen.R;
import com.example.sixinwen.adapter.FirstPageMessageAdapter;
import com.example.sixinwen.adapter.FirstPageNewsAdapter;
import com.example.sixinwen.utils.MessageItem;
import com.example.sixinwen.utils.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kakarotto on 3/18/15.
 */
public class FirstPageMessage extends Fragment {
    private ListView mListView;
    private FirstPageMessageAdapter mAdapter;
    private List<MessageItem> messageItemList;
    private List<AVObject> messagelist;//unsure
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View newsLayout = inflater.inflate(R.layout.first_page_message, container,
                false);
        return newsLayout;
    }
    @Override
    public void onStart() {
        super.onStart();
        init();
        //mEatDrinkHeadLinearLayout.setVisibility(View.INVISIBLE);
    }
    void init() {
        mListView = (ListView) getActivity().findViewById(R.id.first_page_message_listview);
        messageItemList = new ArrayList<MessageItem>();
        final ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.person);
        mAdapter = new FirstPageMessageAdapter(getActivity(), messageItemList);
        MessageItem messageItem = new MessageItem(imageView, "WRH", "你的评论好亮好亮，是学相声的吗？23333");
        MessageItem messageItem1 = new MessageItem(imageView, "HXR", "为什么你这么脑残＝ ＝");
        MessageItem messageItem2 = new MessageItem(imageView, "CQY", "6666666");
        MessageItem messageItem3 = new MessageItem(imageView, "GZY", "呵呵，有本事出来单挑");
        messageItemList.add(messageItem);
        messageItemList.add(messageItem1);
        messageItemList.add(messageItem2);
        messageItemList.add(messageItem3);
        mListView.setAdapter(mAdapter);
    }
}
