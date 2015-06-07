package com.example.sixinwen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ActionMenuView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ${Fastrun} on ${3/18/15}.
 */
public class StartUp extends Activity{
    private Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.d("debug", "handleMessage方法所在的线程："
                    + Thread.currentThread().getName());

            // Handler处理消息
            //textView.setText("点火！");
// 结束Timer计时器
            if (msg.what == 1) {
                Intent intent = new Intent(StartUp.this, SixinwenActivity.class);
                startActivity(intent);
                finish();
            } else timer.cancel();
        }
    };
    TimerTask task = new TimerTask(){
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.startup_page);
        timer = new Timer(true);
        timer.schedule(task,2000);
    }
}
