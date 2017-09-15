package com.beem24.projects.devafrica.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.adapters.FollowUserAdapter;
import com.beem24.projects.devafrica.util.Util;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cz.msebera.android.httpclient.Header;

/**
 * Created By Adigun Hammed Olalekan
 * 7/8/2017.
 * Beem24, Inc
 */

public class FollowUserActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.rv_follow_user_activity)
    RecyclerView mRecyclerView;
    @BindView(R.id.pw_follow_user_activity)
    ProgressWheel progressWheel;
    @BindView(R.id.swipe_layout_follow_user)
    SwipeRefreshLayout swipeRefreshLayout;

    private FollowUserAdapter followUserAdapter;
    private List<User> mData = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_follow_user_activity);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        Requests.get("/top-users", textHttpResponseHandler);
    }

    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("Network response error. Swipe down to refresh.");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                mData.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = User.from(jsonArray.getJSONObject(i));
                    mData.add(user);
                }
                followUserAdapter = new FollowUserAdapter(FollowUserActivity.this, mData);
                mRecyclerView.setAdapter(followUserAdapter);
            }catch (JSONException je) {}
        }

        @Override
        public void onFinish() {
            super.onFinish();
            progressWheel.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_follow_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_item_proceed:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if(Util.isOnline(this)) {
            toast("Device is offline!");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        Requests.get("/top-users", textHttpResponseHandler);
    }
    private TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("Error occurred. Please retry.");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Intent intent = new Intent(FollowUserActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @Override
        public void onStart() {
            super.onStart();
            findViewById(R.id.loading_layout_follow_user).setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            findViewById(R.id.loading_layout_follow_user).setVisibility(View.GONE);
        }
    };
}
