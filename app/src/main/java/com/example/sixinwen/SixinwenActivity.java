package com.example.sixinwen;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.example.sixinwen.fragments.FirstPageHome;
import com.example.sixinwen.fragments.FirstPageMe;
import com.example.sixinwen.fragments.FirstPageMessage;


public class SixinwenActivity extends Activity implements View.OnClickListener{
    private FirstPageHome mFirstPageHome;
    private FirstPageMessage mFirstPageMesaage;
    private FirstPageMe mFirstPageMe;

    private View mHomeLayout;
    private View mMessageLayout;
    private View mMeLayout;

    private ImageView mHomePageImage;
    private ImageView mMessageImage;
    private ImageView mMeImage;
    private TextView mHomePageText;
    private TextView mMessageText;
    private TextView mMeText;
    private FragmentManager mFragmentManager;
    private final static String QING="#00FFFF";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sixinwen);
        AVOSCloud.initialize(this, "epg58oo2271uuupna7b9awz9nzpcxes870uj0j0rzeqkm8mh",
                "xjgx65z5yavhg8nj4r48004prjelkq0fzz9xgricyb2nh0qq");
        AVObject testObject = new AVObject("TestObject");
        //testObject.put("testKey", "sixinwen");
        //testObject.saveInBackground();
        initViews();
        mFragmentManager = getFragmentManager();
        setTabSelection(0);
        AVInstallation.getCurrentInstallation().saveInBackground();
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                    Log.d("保存失败：",e.getMessage());
                }
            }
        });
    }


    private void initViews() {
        //fragments
        mHomeLayout = findViewById(R.id.firstpage_tab1);
        mMessageLayout = findViewById(R.id.firstpage_tab2);
        mMeLayout = findViewById(R.id.firstpage_tab3);
        //tab buttons
        mHomePageImage = (ImageView) findViewById(R.id.homepage_image);
        mMessageImage = (ImageView) findViewById(R.id.find_image);
        mMeImage = (ImageView) findViewById(R.id.me_image);
        mHomePageText = (TextView) findViewById(R.id.homepage_text);
        mMessageText = (TextView) findViewById(R.id.find_text);
        mMeText = (TextView) findViewById(R.id.me_text);
        //set tabs' onClickListener
        mHomeLayout.setOnClickListener(this);
        mMessageLayout.setOnClickListener(this);
        mMeLayout.setOnClickListener(this);
        //

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.firstpage_tab1:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.firstpage_tab2:
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.firstpage_tab3:
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2);
                break;

            default:
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index
     *            每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                //mHomePageImage.setImageResource(R.drawable.home_home);
                mHomePageText.setTextColor(Color.parseColor("#268bfd"));
                //mHomeLayout.setBackgroundColor(Color.GRAY);
                if (mFirstPageHome == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mFirstPageHome = new FirstPageHome();
                    transaction.add(R.id.firstpage_content, mFirstPageHome);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mFirstPageHome);
                }
                break;
            case 1:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
                //mMessageImage.setImageResource(R.drawable.home_find);
                mMessageText.setTextColor(Color.parseColor("#268bfd"));
                //mMessageLayout.setBackgroundColor(Color.GRAY);
                if (mFirstPageMesaage == null) {
                    // 如果mFirstPageFind为空，则创建一个并添加到界面上
                    mFirstPageMesaage = new FirstPageMessage();
                    transaction.add(R.id.firstpage_content, mFirstPageMesaage);
                } else {
                    // 如果mFirstPageFind不为空，则直接将它显示出来
                    transaction.show(mFirstPageMesaage);
                }
                break;

            case 2:
            default:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                //mMeImage.setImageResource(R.drawable.home_pc);
                mMeText.setTextColor(Color.parseColor("#268bfd"));
                //mMeLayout.setBackgroundColor(Color.GRAY);
                if (mFirstPageMe == null) {
                    // 如果SettingFragment为空，则创建一个并添加到界面上
                    mFirstPageMe = new FirstPageMe();
                    transaction.add(R.id.firstpage_content, mFirstPageMe);
                } else {
                    // 如果SettingFragment不为空，则直接将它显示出来
                    transaction.show(mFirstPageMe);
                }
                break;
        }
        transaction.commit();
    }
    private void clearSelection() {
        //mHomePageImage.setImageResource(R.drawable.home_home);
        mHomePageText.setTextColor(Color.BLACK);
        //mMessageImage.setImageResource(R.drawable.home_find);
        mMessageText.setTextColor(Color.BLACK);
        //mMeImage.setImageResource(R.drawable.home_pc);
        mMeText.setTextColor(Color.BLACK);
        mHomeLayout.setBackgroundColor(Color.WHITE);
        mMessageLayout.setBackgroundColor(Color.WHITE);
        mMeLayout.setBackgroundColor(Color.WHITE);
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mFirstPageHome != null) {
            transaction.hide(mFirstPageHome);
        }
        if (mFirstPageMesaage != null) {
            transaction.hide(mFirstPageMesaage);
        }
        if (mFirstPageMe != null) {
            transaction.hide(mFirstPageMe);
        }
    }
}
