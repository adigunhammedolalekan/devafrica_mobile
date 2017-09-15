package com.beem24.projects.devafrica.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RelativeLayout;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.util.Util;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.lang.reflect.Array;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created By Adigun Hammed Olalekan
 * 7/4/2017.
 * Beem24, Inc
 */

public class EntryActivity extends BaseActivity {

    protected CallbackManager callbackManager;

    @BindView(R.id.btn_fb_login_entry_activity)
    RelativeLayout loginButton;

    LoginManager loginManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_entry);
        if(PreferenceManager.getInstance().hasSession()) {
            launch(MainActivity.class);
        }
    }
    @OnClick(R.id.btn_sign_in_entry) public void onLoginClick() {
        startActivity(new Intent(this, LoginActivity.class));
    }
    @OnClick(R.id.btn_join_entry_activity) public void onSignInClick() {
        startActivity(new Intent(this, CreateAccountActivity.class));
    }
    void launch(final Class<?> className) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.startActivity(EntryActivity.this, className);
            }
        }, 2000);
    }
}
