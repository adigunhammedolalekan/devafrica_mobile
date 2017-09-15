package com.beem24.projects.devafrica.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.views.EditTextDialog;
import com.beem24.projects.devafrica.util.Util;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import io.github.mthli.knife.KnifeText;

/**
 * Created By Adigun Hammed Olalekan
 * 7/8/2017.
 * Beem24, Inc
 */

public class ActivityWriteResponse extends BaseActivity {

    @BindView(R.id.response_edt_write_response_activity)
    KnifeText mKnifeText;

    String mPostID = "";
    String mentionJSON = "";
    String opID = "";

    private User toMention = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_write_response);
        Intent intent = getIntent();
        if(intent == null) {
            finish();return;
        }
        mPostID = intent.getStringExtra("post_id");
        opID = intent.getStringExtra("op_id");
        if(intent.hasExtra("to_mention")) {
            mentionJSON = intent.getStringExtra("to_mention");
            toMention = parse(mentionJSON);
        }
        //FirebaseMessaging.getInstance().subscribeToTopic(mPostID);
    }
    User parse(String json) {
        try{
            return User.from(new JSONObject(json));
        }catch (JSONException je) {}
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write_response, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.post_response:
                String text = mKnifeText.getText().toString();
                if(TextUtils.isEmpty(text)) {
                    toast("Cannot post empty response.");
                    return true;
                }
                RequestParams requestParams = new RequestParams();
                requestParams.put("user_id", PreferenceManager.getInstance().getUserID());
                requestParams.put("comment_body", mKnifeText.toHtml());
                requestParams.put("op_id", opID);
                Util.hideKeyboard(this);
                Requests.post("/post/" + mPostID + "/comment", requestParams, textHttpResponseHandler);
        }
        return super.onOptionsItemSelected(item);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            toast("Network response error. Couldn't write post.");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Log.d(DevAfrica.TAG, responseString);
            Intent intent = getIntent();
            intent.putExtra("new_comment", responseString);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            findViewById(R.id.loading_layout_write_response).setVisibility(View.GONE);
        }

        @Override
        public void onStart() {
            super.onStart();
            findViewById(R.id.loading_layout_write_response).setVisibility(View.VISIBLE);
        }
    };
}
