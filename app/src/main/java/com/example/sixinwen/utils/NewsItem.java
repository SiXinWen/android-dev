package com.example.sixinwen.utils;

import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by kakarotto on 3/18/15.
 */
public class NewsItem {
    private ImageView image;
    private String name;
    private String description;
    private int agree;
    private int disagree;
    private Button right;
    public ImageView getImage() {
        return image;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getAgree() {
        return agree;
    }
    public int getDisagree() {
        return disagree;
    }

    public NewsItem(String name, String description, int agree, int disagree, ImageView image) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.agree = agree;
        this.disagree = disagree;
    }
    public NewsItem(NewsItem newsItem){
        this.image = newsItem.getImage();
        this.disagree = newsItem.getDisagree();
        this.agree = newsItem.getAgree();
        this.description = newsItem.getDescription();
    }
}