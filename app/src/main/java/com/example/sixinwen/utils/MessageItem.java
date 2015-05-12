package com.example.sixinwen.utils;

import android.widget.ImageView;

/**
 * Created by wangrunhui on 5/12/15.
 */
public class MessageItem {
    private ImageView avatar;
    private String name;
    private String mAbstract;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getmAbstract() {
        return mAbstract;
    }

    public void setmAbstract(String mAbstract) {
        this.mAbstract = mAbstract;
    }

    public ImageView getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageView avatar) {
        this.avatar = avatar;
    }

    public MessageItem(ImageView avatar, String name, String abstrct) {
        this.avatar = avatar;
        this.name = name;
        this.mAbstract = abstrct;
    }
}
