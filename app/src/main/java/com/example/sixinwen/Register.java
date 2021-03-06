package com.example.sixinwen;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.SignUpCallback;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by ${Fastrun} on ${3/18/15}.
 */
public class Register extends Activity {
    private EditText mUsername;
    private EditText mNickname;
    private EditText mPassword;
    private EditText mEmail;
    private EditText mGender;
    private EditText mPhone;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.user_register);
        initViews();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String nickname = mNickname.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();
                String gender = mGender.getText().toString();
                String phone = mPhone.getText().toString();
                final AVUser user = new AVUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.put("phone", phone);
                user.put("gender", gender);
                user.put("NickName", nickname);
                user.signUpInBackground(new SignUpCallback() {
                    public void done(AVException e) {
                        if (e == null) {
                            // successfully
                            Log.d("register", "successful");
                            Toast.makeText(Register.this, "注册成功", Toast.LENGTH_SHORT);
                            finish();
                        } else {
                            // failed
                            Log.d("register", "failed");
                            Toast.makeText(Register.this, "注册失败，请重新尝试", Toast.LENGTH_SHORT);
                        }
                    }
                });

                Map<String,Object> parameters = new Hashtable<>();
                parameters.put("InsID", username);
                user.signUpInBackground(new SignUpCallback() {
                    public void done(AVException e) {
                        if (e == null) {
                            // successfully
                            Log.d("register", "successful");
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // failed
                            Log.d("register", "failed");
                            Toast.makeText(getApplicationContext(), "注册失败，请重新尝试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

 /*               AVCloud.callFunctionInBackground("InsSignUp", parameters, new FunctionCallback() {
                    public void done(Object object, AVException e) {
                        if (e == null) {
                            Log.d("sign up", "secceed!" + object.toString());



                        } else {
                            e.printStackTrace();
                        }
                    }
                });
*/
            }
        });
    }

    public void initViews() {
        mUsername = (EditText) findViewById(R.id.username);
        mNickname = (EditText) findViewById(R.id.nickname);
        mPassword = (EditText) findViewById(R.id.password);
        mEmail = (EditText) findViewById(R.id.email);
        mGender = (EditText) findViewById(R.id.gender);
        mPhone = (EditText) findViewById(R.id.phone);
        register = (Button) findViewById(R.id.register);
    }
}
