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
import android.widget.TextView;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Post;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.adapters.PostAdapterImpl;
import com.beem24.projects.devafrica.ui.adapters.TopicAdapter;
import com.beem24.projects.devafrica.ui.views.EndLessScrollListener;
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

public class PostFragmentImpl extends BaseFragment {

    @BindView(R.id.pw_user_posts_profile)
    ProgressWheel progressWheel;
    @BindView(R.id.rv_post_impl_fragment)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_post_layout_post_impl)
    LinearLayout noPostLayout;
    @BindView(R.id.bio_big_tv_profile)
    TextView bioTextView;
    @BindView(R.id.date_joined_big_profile)
    TextView timeJoined;
    @BindView(R.id.tv_location_profile)
    TextView mTvLocation;
    @BindView(R.id.tv_language_profile)
    TextView languageStackTv;
    @BindView(R.id.tv_fb_profile)
    TextView faceBook;
    @BindView(R.id.tv_twitter_profile)
    TextView twitter;
    @BindView(R.id.tv_github_profile)
    TextView github;
    @BindView(R.id.layout_fb_profile)
    LinearLayout layoutFB;
    @BindView(R.id.layout_twitter_profile)
    LinearLayout layoutTwitter;
    @BindView(R.id.layout_github_profile)
    LinearLayout layoutGithub;


    private List<Post> mPostList;
    private TopicAdapter mPostAdapter;


    private User mCurrentUser;
    private EndLessScrollListener endLessScrollListener;
    private LinearLayoutManager linearLayoutManager;
    private volatile boolean mLoading = false;

    public static PostFragmentImpl newInstance(String id) {

        Bundle args = new Bundle();
        args.putString("raw_user", id);
        PostFragmentImpl fragment = new PostFragmentImpl();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_post_fragment_impl, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        endLessScrollListener = new EndLessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                paginate(totalItemsCount);
            }
        };

        mRecyclerView.addOnScrollListener(endLessScrollListener);
        String data = getArguments().getString("raw_user");
        Log.d(DevAfrica.TAG, data);
        mCurrentUser = parse(getArguments().getString("raw_user"));
        render(mCurrentUser);
        getPosts();
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
    void getPosts() {
        if(!Util.isOnline(getActivity())) {
            return;
        }
        Requests.get("/user/" + mCurrentUser.ID + "/posts/0", textHttpResponseHandler);
    }
    void render(User user) {
        bioTextView.setText(user.bio.isEmpty() ? "---" : user.bio);
        //timeJoined.setText(user.);
        mTvLocation.setText(user.country.isEmpty() ? "- not available" : user.country);
        languageStackTv.setText(user.mStack.isEmpty() ?  "- not available" : user.mStack);

        if(!user.twitter.isEmpty()) {
            twitter.setText(user.twitter);
        }else {
            layoutTwitter.setVisibility(View.GONE);
        }
        if(!user.facebook.isEmpty()) {
            faceBook.setText(user.facebook);
        }else {
            layoutFB.setVisibility(View.GONE);
        }
        if(!user.github.isEmpty()) {
            github.setText(user.github);
        }else {
            layoutGithub.setVisibility(View.GONE);
        }
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.d(DevAfrica.TAG, "ERROR" + responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Log.d(DevAfrica.TAG, responseString);
            mPostList = JSON.posts(responseString);
            if(mPostList.isEmpty()) {
                noPostLayout.setVisibility(View.VISIBLE);
            }else {
                noPostLayout.setVisibility(View.GONE);
                mPostAdapter = new TopicAdapter(getActivity(), mPostList);
                mRecyclerView.setAdapter(mPostAdapter);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            progressWheel.setVisibility(View.GONE);
        }

        @Override
        public void onStart() {
            super.onStart();
            progressWheel.setVisibility(View.VISIBLE);
        }
    };
    void paginate(int count) {

        if(!Util.isOnline(getActivity()) || mLoading)
            return;

        mLoading = true;
        Requests.get("/user/" + PreferenceManager.getInstance().getUserID() + "/posts/" + count, paginateResponseHandler);
    }
    private TextHttpResponseHandler paginateResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            List<Post> posts = JSON.posts(responseString);
            if(posts.size() > 0) {
                for (Post post : posts) {
                    mPostList.add(post);
                }
                int count = mPostAdapter.getItemCount();
                mPostAdapter.notifyItemRangeChanged(count, mPostList.size() - 1);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mLoading = false;
        }
    };
}
