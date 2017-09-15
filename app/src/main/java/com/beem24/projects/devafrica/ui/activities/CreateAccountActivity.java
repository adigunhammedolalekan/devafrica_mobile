package com.beem24.projects.devafrica.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
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
 * Created By Adigun Hammed Olalekan
 * 7/4/2017.
 * Beem24, Inc
 */

public class CreateAccountActivity extends BaseActivity {

    @BindView(R.id.edt_email_address_sign_up)
    MaterialEditText emailEditText;
    @BindView(R.id.edt_password_sign_up)
    MaterialEditText passwordEditText;
    @BindView(R.id.edt_username_address_sign_up)
    MaterialEditText usernameEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

    }
    @OnClick(R.id.btn_sign_up_join) public void onSignUp() {
        if(!Patterns.EMAIL_ADDRESS.matcher(Util.text(emailEditText)).matches()) {
            emailEditText.setError("Invalid email address.");
            return;
        }
        if(Util.text(usernameEditText).length() < 3) {
            usernameEditText.setError("Username is too short.");
            return;
        }
        if(Util.text(passwordEditText).length() < 5) {
            passwordEditText.setError("Invalid password. Too weak");
            return;
        }
        Util.hideKeyboard(this);
        AsyncHttpClient asyncHttpClient = Requests.asyncHttpClient;
        RequestParams requestParams = new RequestParams();
        requestParams.put("email", Util.text(emailEditText));
        requestParams.put("username", Util.text(usernameEditText));
        requestParams.put("password", Util.text(passwordEditText));
        asyncHttpClient.post(Requests.BASE_URL + "/sign-up", requestParams, textHttpResponseHandler);
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
                showDialog("Error", Html.fromHtml(jsonObject.getString("message")).toString());
            }catch (JSONException je) {
                Log.d(DevAfrica.TAG, "ERROR", je);
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            try {
                JSONObject jsonObject = new JSONObject(responseString);
                String token = jsonObject.getString("token");
                PreferenceManager.getInstance().saveToken(token);
                PreferenceManager.getInstance().save(jsonObject.getJSONObject("data"));
                PreferenceManager.getInstance().save(jsonObject.getJSONObject("data").getString("user_id"));

                Util.startActivity(CreateAccountActivity.this, UpdateAccountActivity.class);
            }catch (JSONException je) {
                Log.d(DevAfrica.TAG, "ERROR", je);
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            findViewById(R.id.loading_layout_sign_up).setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            findViewById(R.id.loading_layout_sign_up).setVisibility(View.GONE);
        }
    };
}
