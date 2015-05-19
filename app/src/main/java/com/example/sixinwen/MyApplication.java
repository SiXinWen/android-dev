package com.example.sixinwen;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by ${Fastrun} on ${3/18/15}.
 */
public class MyApplication extends Application {
    public void onCreate(){
        AVOSCloud.initialize(this, "epg58oo2271uuupna7b9awz9nzpcxes870uj0j0rzeqkm8mh", "xjgx65z5yavhg8nj4r48004prjelkq0fzz9xgricyb2nh0qq");
    }
}
