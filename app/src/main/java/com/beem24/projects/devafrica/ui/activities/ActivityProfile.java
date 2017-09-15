package com.beem24.projects.devafrica.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Queue;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.fragments.PostFragmentImpl;
import com.beem24.projects.devafrica.ui.fragments.UserFollowerFragment;
import com.beem24.projects.devafrica.ui.fragments.UserFollowingFragment;
import com.beem24.projects.devafrica.util.Util;
import com.bumptech.glide.Glide;
import com.loopj.android.http.TextHttpResponseHandler;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/7/2017.
 * Beem24, Inc
 */

public class ActivityProfile extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.username_tv_profile)
    TextView mUsername;
    @BindView(R.id.bio_tv_profile)
    TextView bioTextView;
    @BindView(R.id.time_joined_tv_profile)
    TextView mTimeJoined;
    @BindView(R.id.tv_follower_count_profile_stat)
    TextView followerCount;
    @BindView(R.id.tv_following_count_profile_stat)
    TextView followingCount;
    @BindView(R.id.tv_post_count_profile_stat)
    TextView postCount;
    @BindView(R.id.swipe_layout_profile)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.btn_follow_user_profile)
    Button mFollowButton;
    @BindView(R.id.iv_user_dp_profile)
    CircleImageView dp;
    @BindView(R.id.tab_user_posts_profile)
    MaterialIconView mPostMaterialIconView;
    @BindView(R.id.tab_user_followers_profile)
    MaterialIconView mFollowerMaterialIconView;
    @BindView(R.id.tab_user_following_profile)
    MaterialIconView mFolloingMaterialIconView;

    private User currentProfile;

    private FragmentManager mFragmentManager;

    String JSON = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_profile);

        Intent intent = getIntent();
        if(intent == null) {
            finish();
            return;
        }
        mFollowButton.setEnabled(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mFragmentManager = getSupportFragmentManager();
        JSON = intent.getStringExtra("raw_user");
        currentProfile = parse(JSON);
        getProfile(currentProfile.ID);
        mUsername.setText(String.format("%s%s", "~", currentProfile.username));
        bioTextView.setText("-");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(currentProfile.username);
        }
        setPostTabActive();
    }
    User parse(String json) {
        try {
            return User.from(new JSONObject(json));
        }catch (JSONException je) {}
        return new User();
    }
    void getProfile(String user) {
        if(!Util.isOnline(this)) {
            Snackbar.make(postCount, "Device is offline", Snackbar.LENGTH_INDEFINITE).show();
            return;
        }
        Requests.get("/user/" + user + "/profile", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                toast("Network response unknown");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(DevAfrica.TAG, responseString + "_");
                try {

                    currentProfile = User.from(new JSONObject(responseString).getJSONObject("data"));
                    render(currentProfile);

                    mFollowButton.setEnabled(true);
                }catch (JSONException je) {
                    Log.d(DevAfrica.TAG, "ERROR", je);
                }
            }
            @Override
            public void onFinish() {
                super.onFinish();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onStart() {
                super.onStart();
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }
    void render(User user) {
        if(!TextUtils.isEmpty(user.bio)) {
            bioTextView.setText(user.bio);
        }
        if(!user.mPhoto.trim().isEmpty()) {
            Glide.with(this)
                    .load(user.mPhoto)
                    .error(R.color.divider)
                    .placeholder(R.color.divider).dontAnimate().into(dp);
        }
        followerCount.setText(String.valueOf(user.followerCount));
        followingCount.setText(String.valueOf(user.followingCount));
        postCount.setText(String.valueOf(user.mPostCount));

        if(user.selected) {
            mFollowButton.setBackgroundResource(R.drawable.btn_bg_green);
            mFollowButton.setText("FOLLOWING");
            mFollowButton.setTextColor(ContextCompat.getColor(this, R.color.green));
        }else {
            mFollowButton.setBackgroundResource(R.drawable.btn_bg);
            mFollowButton.setText("FOLLOW");
            mFollowButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        mTimeJoined.setText(user.timeJoined);

        if(TextUtils.equals(user.ID, PreferenceManager.getInstance().getUserID())) {
            mFollowButton.setVisibility(View.GONE);
        }
    }
    void setPostTabActive() {
        mPostMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        mFolloingMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFollowerMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFragmentManager.beginTransaction().replace(R.id.profile_framelayout_container, PostFragmentImpl.newInstance(JSON),
                PostFragmentImpl.class.getSimpleName()).commit();
    }
    void setFollowingTabActive() {
        mFolloingMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        mPostMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFollowerMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFragmentManager.beginTransaction().replace(R.id.profile_framelayout_container, UserFollowingFragment.newInstance(JSON),
                UserFollowingFragment.class.getSimpleName()).commit();
    }
    void setFollowerTabActive() {
        mFollowerMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        mPostMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFolloingMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFragmentManager.beginTransaction().replace(R.id.profile_framelayout_container, UserFollowerFragment.newInstance(JSON),
                UserFollowerFragment.class.getSimpleName()).commit();
    }
    Activity getActivity() {
        return this;
    }
    @OnClick(R.id.tab_user_following_profile) public void onFollowingClick() {
        setFollowingTabActive();
    }
    @OnClick(R.id.tab_user_followers_profile) public void onFollowerClick() {
        setFollowerTabActive();
    }
    @OnClick(R.id.tab_user_posts_profile) public void onPostClick() {
        setPostTabActive();
    }

    @Override
    public void onRefresh() {
        if(Util.isOnline(this)) {
            getProfile(currentProfile.ID);
        }else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    @OnClick(R.id.btn_follow_user_profile) public void onFollowButtonClick() {
        if(currentProfile.selected) {
            new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                    .setTitle("Unfollow " + currentProfile.username + " ?")
                    .setMessage("Do you want to unFollow " + currentProfile.username + " ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Queue queue = new Queue();
                            queue.mURL = Requests.BASE_URL + "/user/unfollow";
                            queue.mRequestParams.put("master_id", currentProfile.ID);
                            queue.mRequestParams.put("user_id", PreferenceManager.getInstance().getUserID());

                            DevAfrica.getApp().addJob(queue);
                            currentProfile.selected = false;
                            mFollowButton.setText("FOLLOW");
                            mFollowButton.setTextColor(ContextCompat.getColor(ActivityProfile.this, R.color.white));
                            mFollowButton.setBackgroundResource(R.drawable.btn_bg);
                        }
                    }).setNegativeButton("CANCEL", null).create().show();
        }else {
            Queue queue = new Queue();
            queue.mURL = Requests.BASE_URL + "/user/follow";
            queue.mRequestParams.put("master_id", currentProfile.ID);
            queue.mRequestParams.put("user_id", PreferenceManager.getInstance().getUserID());

            DevAfrica.getApp().addJob(queue);
            currentProfile.selected = true;
            mFollowButton.setText("FOLLOWING");
            mFollowButton.setTextColor(ContextCompat.getColor(this, R.color.green));
            mFollowButton.setBackgroundResource(R.drawable.btn_bg_green);
        }
    }
    @OnClick(R.id.layout_post_stat_profile) public void onPostStatClick() {
        setPostTabActive();
    }
    @OnClick(R.id.layout_follower_stat_profile) public void onFollowerStatClick() {
        setFollowerTabActive();
    }
    @OnClick(R.id.layout_following_stat_profile) public void onFollowingStatClick() {
        setFollowingTabActive();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
