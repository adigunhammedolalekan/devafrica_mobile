package com.beem24.projects.devafrica.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.util.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 7/4/2017.
 */

public class LoginActivity extends BaseActivity{

    @BindView(R.id.edt_email_address_login)
    MaterialEditText emailEditText;
    @BindView(R.id.edt_password_login)
    MaterialEditText passwordEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    @OnClick(R.id.btn_sign_in_login) public void onLogin() {
        if(!Patterns.EMAIL_ADDRESS.matcher(Util.text(emailEditText)).matches()) {
            emailEditText.setError("Invalid email address.");
            return;
        }
        if(Util.text(passwordEditText).length() < 5) {
            passwordEditText.setError("Invalid password. Too short.");
            return;
        }
        Util.hideKeyboard(this);
        RequestParams requestParams = new RequestParams();
        requestParams.put("username", Util.text(emailEditText));
        requestParams.put("password", Util.text(passwordEditText));
        AsyncHttpClient asyncHttpClient = Requests.asyncHttpClient;
        asyncHttpClient.post(Requests.BASE_URL + "/authenticate", requestParams, textHttpResponseHandler);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if(responseString == null || responseString.isEmpty()) {
                toast("Error response from remote server.");
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                showDialog("Error", jsonObject.getString("message"));
            }catch (JSONException je) {
                Log.d(DevAfrica.TAG, "ERROR", je);
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Log.d(DevAfrica.TAG, responseString + "_");
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                String id = jsonObject.getString("token");
                PreferenceManager.getInstance().saveToken(id);
                PreferenceManager.getInstance().save(jsonObject.getJSONObject("data"));
                PreferenceManager.getInstance().save(jsonObject.getJSONObject("data").getString("user_id"));
                toast("Success");
                Util.startActivity(LoginActivity.this, MainActivity.class);
            }catch (JSONException je) {}
        }

        @Override
        public void onStart() {
            super.onStart();
            findViewById(R.id.loading_layout_login).setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            findViewById(R.id.loading_layout_login).setVisibility(View.GONE);
        }
    };
}
