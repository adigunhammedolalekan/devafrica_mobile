package com.beem24.projects.devafrica.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.adapters.UserListAdapter;
import com.beem24.projects.devafrica.util.JSON;
import com.beem24.projects.devafrica.util.Util;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created By Adigun Hammed Olalekan
 * 7/20/2017.
 * Beem24, Inc
 */

public class UserFollowingFragment extends BaseFragment {

    @BindView(R.id.rv_user_following_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.pw_following_fragment)
    ProgressWheel progressWheel;
    @BindView(R.id.error_layout_user_following)
    LinearLayout mLinearLayout;
    
    private List<User> mData = new ArrayList<>();
    private UserListAdapter userListAdapter;

    User mUser;

    public static UserFollowingFragment newInstance(String id) {
        
        Bundle args = new Bundle();
        args.putString("raw_user", id);
        UserFollowingFragment fragment = new UserFollowingFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_following_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setNestedScrollingEnabled(false);
        mUser = parse(getArguments().getString("raw_user"));
        getFollowing();
    }
    void getFollowing() {
        if(Util.isOnline(getActivity())) {
            Requests.get("/user/" + mUser.ID + "/following", textHttpResponseHandler);
        }
    }
    User parse(String JSON) {
        //json string can come in two version.
        try {
            return User.from(new JSONObject(JSON).getJSONObject("data"));
        }catch (JSONException e) {
            try {
                return User.from(new JSONObject(JSON));
            }catch (JSONException je) {}
        }
        return new User();
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            mLinearLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            mLinearLayout.setVisibility(View.GONE);

            mData = JSON.users(responseString);
            userListAdapter = new UserListAdapter(mData, getActivity());
            mRecyclerView.setAdapter(userListAdapter);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            progressWheel.setVisibility(View.GONE);
        }
    };
    @OnClick(R.id.error_layout_user_following) public void onErrorClick() {
        getFollowing();
    }
}
