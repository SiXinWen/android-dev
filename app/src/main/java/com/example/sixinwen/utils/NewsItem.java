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
    private int commentNumber;
    private double agree;
    private double disagree;
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
    public double getAgree() {
        return agree;
    }
    public double getDisagree() {
        return disagree;
    }

    public NewsItem(String name, String description, double agree, double disagree, ImageView image, int n) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.agree = agree;
        this.disagree = disagree;
        this.commentNumber = n;
    }
    public NewsItem(NewsItem newsItem){
        this.image = newsItem.getImage();
        this.disagree = newsItem.getDisagree();
        this.agree = newsItem.getAgree();
        this.description = newsItem.getDescription();
    }

    public int getCommentNumber() {
        return commentNumber;
    }
}