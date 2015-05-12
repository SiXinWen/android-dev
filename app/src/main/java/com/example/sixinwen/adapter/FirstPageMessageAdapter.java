package com.example.sixinwen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sixinwen.R;
import com.example.sixinwen.utils.MessageItem;

import java.awt.font.TextAttribute;
import java.util.List;

/**
 * Created by wangrunhui on 5/12/15.
 */
public class FirstPageMessageAdapter extends BaseAdapter {
    protected Context mContext;
    protected List<MessageItem> messageItemList;

    public FirstPageMessageAdapter(Context context, List<MessageItem> list) {
        mContext = context;
        messageItemList = list;
    }

    @Override
    public int getCount() {
        return messageItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.first_page_message_listview_item, null);
            holder = new ViewHolder();

            holder.avatar = (ImageView) convertView.findViewById(R.id.first_page_message_item_pic);
            holder.name = (TextView) convertView.findViewById(R.id.first_page_message_item_person_name);
            holder.mAbstract = (TextView) convertView.findViewById(R.id.first_page_message_item_abstract);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MessageItem messageItem = messageItemList.get(position);
        holder.avatar.setImageDrawable(messageItem.getAvatar().getDrawable());
        holder.name.setText(messageItem.getName());
        holder.mAbstract.setText(messageItem.getmAbstract());
        return convertView;
    }


    class ViewHolder {
        public ImageView avatar;
        public TextView name;
        public TextView mAbstract;
    }
}
