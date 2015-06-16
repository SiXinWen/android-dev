package com.example.sixinwen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

import java.lang.reflect.AccessibleObject;

/**
 * Created by ${Fastrun} on ${3/18/15}.
 */
public class Login extends Activity {
    private EditText name;
    private EditText password;
    private Button login;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.user_login);
        AVOSCloud.setDebugLogEnabled(true);
        initViews();
    }

    private void initViews() {
        name = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    Toast.makeText(Login.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().length() == 0) {
                    //Toast.makeText(Login.this, "密码名不能为空", Toast.LENGTH_SHORT).show();
                    //return;
                }
                AVUser.logInInBackground(name.getText().toString(), password.getText().toString(), new LogInCallback() {
                    public void done(AVUser user, AVException e) {
                        if (user != null) {
                            // 登录成功
                            Log.d("Login", "succeed!");
                            MyApplication.setUsername(name.getText().toString());
                            finish();
                        } else {
                            // 登录失败
                            Log.d("Login", "failed!");
                        }
                    }
                });
            }
        });
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }
}
