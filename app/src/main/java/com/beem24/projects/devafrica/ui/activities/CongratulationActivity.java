package com.beem24.projects.devafrica.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.Post;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.OnClick;

/**
 * Created By Adigun Hammed Olalekan
 * 7/16/2017.
 * Beem24, Inc
 */

public class CongratulationActivity extends BaseActivity {

    String JSON = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_post_published_success);

        Intent intent = getIntent();
        if(intent == null) {
            finish();
            return;
        }
        JSON = intent.getStringExtra("_data_");
        try {
            Post post = Post.from(new JSONObject(JSON));
            FirebaseMessaging.getInstance().subscribeToTopic(post.postID);
        } catch (JSONException e) {
            Log.d(DevAfrica.TAG, "ERROR", e);
        }
    }
    @OnClick(R.id.btn_go_back) public void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
