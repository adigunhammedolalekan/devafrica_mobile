package com.beem24.projects.devafrica.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.adapters.PostAdapterImpl;
import com.beem24.projects.devafrica.util.Util;
import com.bumptech.glide.Glide;
import com.loopj.android.http.TextHttpResponseHandler;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/7/2017.
 * Beem24, Inc
 */

public class ProfileFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{


    @BindView(R.id.swipe_layout_profile)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.iv_user_dp_profile)
    CircleImageView dp;
    @BindView(R.id.btn_follow_user_profile)
    Button follow;
    @BindView(R.id.username_tv_profile)
    TextView usernameTextView;
    @BindView(R.id.tv_post_count_profile_stat)
            TextView postCount;
    @BindView(R.id.tv_follower_count_profile_stat)
            TextView followerCount;
    @BindView(R.id.tv_following_count_profile_stat)
            TextView followingCount;
    @BindView(R.id.time_joined_tv_profile)
            TextView timeJoined;
    @BindView(R.id.bio_tv_profile)
            TextView bioTextView;
    @BindView(R.id.tab_user_posts_profile)
            MaterialIconView mPostMaterialIconView;
    @BindView(R.id.tab_user_followers_profile)
            MaterialIconView mFollowerMaterialIconView;
    @BindView(R.id.tab_user_following_profile)
            MaterialIconView mFolloingMaterialIconView;

    User currentUser;
    FragmentManager mFragmentManager;

    String userJSON = "";

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentManager = getFragmentManager();
        follow.setVisibility(View.GONE);
        userJSON = PreferenceManager.getInstance().userJSON();
        currentUser = PreferenceManager.getInstance().getUser();
        render(currentUser);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        setPostTabActive();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    void render(User user) {
        if(user.bio.trim().isEmpty()) {
            bioTextView.setText("---");
        }else {
            bioTextView.setText(user.bio);
        }
        if(!user.mPhoto.isEmpty()) {
            Glide.with(this)
                    .load(user.mPhoto).placeholder(R.color.divider).error(R.color.divider)
                    .dontAnimate().into(dp);
        }
        timeJoined.setText(user.timeJoined);
        postCount.setText(String.valueOf(user.mPostCount));
        followerCount.setText(String.valueOf(user.followerCount));
        followingCount.setText(String.valueOf(user.followingCount));
        usernameTextView.setText(String.format(Locale.getDefault(), "%s%s", "~", user.username));
    }
    void setPostTabActive() {
        mPostMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        mFolloingMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFollowerMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFragmentManager.beginTransaction().replace(R.id.profile_framelayout_container,
                PostFragmentImpl.newInstance(userJSON),
                PostFragmentImpl.class.getSimpleName()).commit();
    }
    void setFollowingTabActive() {
        mFolloingMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        mPostMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFollowerMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFragmentManager.beginTransaction().replace(R.id.profile_framelayout_container, UserFollowingFragment.newInstance(userJSON),
                UserFollowingFragment.class.getSimpleName()).commit();
    }
    void setFollowerTabActive() {
        mFollowerMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        mPostMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFolloingMaterialIconView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mFragmentManager.beginTransaction().replace(R.id.profile_framelayout_container, UserFollowerFragment.newInstance(userJSON),
                UserFollowerFragment.class.getSimpleName()).commit();
    }
    @OnClick(R.id.tab_user_posts_profile) public void onPostsClick() {
        setPostTabActive();
    }
    @OnClick(R.id.tab_user_followers_profile) public void onFollowerTabClick() {
        setFollowerTabActive();
    }
    @OnClick(R.id.tab_user_following_profile) public void onFollowingTabClick() {
        setFollowingTabActive();
    }

    @Override
    public void onRefresh() {
        if(Util.isOnline(getActivity())) {
            Requests.get("/user/" + PreferenceManager.getInstance().getUserID() + "/profile", textHttpResponseHandler);
        }
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("Network response unknown");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                PreferenceManager.getInstance().save(jsonObject);
                User user = User.from(jsonObject);

                render(user);
            }catch (JSONException je) {}
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };
}
