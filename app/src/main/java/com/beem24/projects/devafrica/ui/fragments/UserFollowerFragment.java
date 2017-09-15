package com.beem24.projects.devafrica.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.adapters.FollowUserAdapter;
import com.beem24.projects.devafrica.ui.adapters.UserListAdapter;
import com.beem24.projects.devafrica.util.JSON;
import com.beem24.projects.devafrica.util.Util;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import cz.msebera.android.httpclient.Header;

/**
 * Created By Adigun Hammed Olalekan
 * 7/9/2017.
 * Beem24, Inc
 */

public class UserFollowerFragment extends BaseFragment{

    @BindView(R.id.rv_followed_user_layout)
    RecyclerView mRecyclerView;
    @BindView(R.id.pw_followed_user_layout)
    ProgressWheel progressWheel;
    @BindView(R.id.no_follower_layout_followed_user_layout_impl)
    LinearLayout noFollower;

    UserListAdapter userListAdapter;
    List<User> users;

    String JSON = "";

    private static final String JSON_STATE_KEY = "_key_";

    private User mUser;

    public static UserFollowerFragment newInstance(String id) {

        Bundle args = new Bundle();
        args.putString("raw_user", id);

        UserFollowerFragment fragment = new UserFollowerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.followed_user_layout_impl, container, false);

        if(savedInstanceState == null)
            return view;

        JSON = savedInstanceState.getString(JSON_STATE_KEY);
        return view;
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
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setNestedScrollingEnabled(false);

        mUser = parse(getArguments().getString("raw_user"));
        getFollowers();
    }
    void getFollowers() {

        Log.d(DevAfrica.TAG, mUser.ID);
        if(Util.isOnline(getActivity())) {
            Requests.get("/user/" + mUser.ID + "/followers", textHttpResponseHandler);
        }
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            users = com.beem24.projects.devafrica.util.JSON.users(responseString);
            if(users.isEmpty()) {
                noFollower.setVisibility(View.VISIBLE);
            }else {
                noFollower.setVisibility(View.GONE);
                userListAdapter = new UserListAdapter(users, getActivity());
                mRecyclerView.setAdapter(userListAdapter);
            }
        }

        @Override
        public void onFinish() {
            progressWheel.setVisibility(View.GONE);
        }

    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(JSON_STATE_KEY, JSON);
    }
}
