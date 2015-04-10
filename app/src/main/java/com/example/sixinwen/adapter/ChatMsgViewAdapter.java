package com.example.sixinwen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sixinwen.R;
import com.example.sixinwen.utils.ChatMsgEntity;

import java.util.List;

/**
 * Created by kakarotto on 15-3-31.
 */
public class ChatMsgViewAdapter extends BaseAdapter {

    //ListView视图的内容由IMsgViewType决定
    public static interface IMsgViewType
    {
        //对方发来的信息
        int IMVT_COM_MSG = 0;
        //自己发出的信息
        int IMVT_TO_MSG = 1;
    }

    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();
    private List<ChatMsgEntity> data;
    private Context context;
    private LayoutInflater mInflater;

    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> data) {
        this.context = context;
        this.data = data;
        mInflater = LayoutInflater.from(context);
    }

    //获取ListView的项个数
    public int getCount() {
        return data.size();
    }

    //获取项
    public Object getItem(int position) {
        return data.get(position);
    }

    //获取项的ID
    public long getItemId(int position) {
        return position;
    }

    //获取项的类型
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        ChatMsgEntity entity = data.get(position);

        if (entity.getMsgType())
        {
            return IMsgViewType.IMVT_COM_MSG;
        }else{
            return IMsgViewType.IMVT_TO_MSG;
        }

    }

    //获取项的类型数
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 2;
    }

    //获取View
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMsgEntity entity = data.get(position);
        boolean isComMsg = entity.getMsgType();

        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            if (isComMsg)
            {
                //如果是对方发来的消息，则显示的是左气泡
                convertView = mInflater.inflate(R.layout.chatting_msg_text_left_item, null);
            }else{
                //如果是自己发出的消息，则显示的是右气泡
                convertView = mInflater.inflate(R.layout.chatting_msg_text_right_item, null);
            }

            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            viewHolder.isComMsg = isComMsg;

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvSendTime.setText(entity.getDate());
        viewHolder.tvUserName.setText(entity.getName());
        viewHolder.tvContent.setText(entity.getText());

        return convertView;
    }

    //通过ViewHolder显示项的内容
    static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public boolean isComMsg = true;
    }

}