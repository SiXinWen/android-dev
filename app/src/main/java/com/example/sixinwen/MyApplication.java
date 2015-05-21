package com.example.sixinwen;

import android.app.Application;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.SaveCallback;

/**
 * Created by ${Fastrun} on ${3/18/15}.
 */
public class MyApplication extends Application {
    public void onCreate(){
        AVOSCloud.initialize(this, "epg58oo2271uuupna7b9awz9nzpcxes870uj0j0rzeqkm8mh", "xjgx65z5yavhg8nj4r48004prjelkq0fzz9xgricyb2nh0qq");
//        AVInstallation.getCurrentInstallation().saveInBackground();
 /*       AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                    Log.d("保存失败：", e.getMessage());
                }
            }
        });
 */   }
}
