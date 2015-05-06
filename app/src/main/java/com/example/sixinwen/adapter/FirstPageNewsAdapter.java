package com.example.sixinwen.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sixinwen.R;
import com.example.sixinwen.utils.NewsItem;

import java.util.List;

/**
 * Created by kakarotto on 3/18/15.
 */
public class FirstPageNewsAdapter extends BaseAdapter{
    protected Context mContext;
    protected List<NewsItem> newsItemList;
    public FirstPageNewsAdapter(Context context, List<NewsItem> list) {
        mContext = context;
        newsItemList = list;
    }
    @Override
    public int getCount() {
        return newsItemList.size();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Log.v("BaseAdapterSchoolNews", "getView " + position + " " + convertView);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.first_page_news_item, null);//reuse the layout of first_page_news_item
            holder = new ViewHolder();
                /*得到各个控件的对象*/
            holder.title = (TextView) convertView.findViewById(R.id.first_page_news_item_intro);//reuse, so is the following
            holder.description = (TextView) convertView.findViewById(R.id.first_page_news_item_description);
        //    holder.right = (Button) convertView.findViewById(R.id.first_page_news_item_right);
            holder.agree = (TextView) convertView.findViewById(R.id.first_page_news_item_agree);
            holder.disagree = (TextView) convertView.findViewById(R.id.first_page_news_item_disagree);
            holder.image = (ImageView) convertView.findViewById(R.id.first_page_news_item_pic);
            convertView.setTag(holder); //绑定ViewHolder对象
        }
        else {
            holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
        }
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); convertView.setLayoutParams(lp);
        convertView.setMinimumHeight(80);
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/

            /*为Button添加点击事件*/
        NewsItem newsItem = newsItemList.get(position);
        holder.image.setImageDrawable(newsItem.getImage().getDrawable());
        holder.description.setText(newsItem.getDescription());
        holder.title.setText(newsItem.getName());
        //holder.agree.setText(newsItem.getAgree());
        //holder.disagree.setText(newsItem.getDisagree());



        return convertView;
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        //return infoItemList.get(position);
        return newsItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    class ViewHolder {
        public TextView title;
        public TextView description;
        public ImageView image;
        public TextView agree;
        public TextView disagree;
        public Button right;
    }
}

