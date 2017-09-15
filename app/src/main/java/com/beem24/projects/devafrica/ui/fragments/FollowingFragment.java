package com.beem24.projects.devafrica.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Post;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.adapters.TopicAdapter;
import com.beem24.projects.devafrica.ui.views.EndLessScrollListener;
import com.beem24.projects.devafrica.util.JSON;
import com.beem24.projects.devafrica.util.Util;
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
 * 7/7/2017.
 * Beem24, Inc
 */

public class FollowingFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.rv_following_fragment)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_no_network)
    LinearLayout mLinearLayout;
    @BindView(R.id.layout_no_follower_post)
    LinearLayout noFollowerPost;
    @BindView(R.id.pw_following_fragment)
    ProgressWheel progressWheel;
    @BindView(R.id.swipe_layout_following_fragment)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TopicAdapter mTopicAdapter;
    private List<Post> mPostList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;

    private volatile boolean mLoading = false;

    public static FollowingFragment newInstance() {

        FollowingFragment fragment = new FollowingFragment();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_following_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView.addOnScrollListener(new EndLessScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                paginate(totalItemsCount);
            }
        });

        getPosts();
    }

    @Override
    public void onRefresh() {
        getPosts();
    }
    void getPosts() {
        if(!Util.isOnline(getActivity())) {
            Snackbar.make(mRecyclerView, "Device is offline", Snackbar.LENGTH_INDEFINITE).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        Requests.get("/user/" + PreferenceManager.getInstance().getUserID() + "/following/posts/0", textHttpResponseHandler);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            mPostList.clear();
            mPostList = JSON.posts(responseString);
            if(mPostList.size() <= 0) {
                noFollowerPost.setVisibility(View.VISIBLE);
            }else {
                noFollowerPost.setVisibility(View.GONE);
                mTopicAdapter = new TopicAdapter(getActivity(), mPostList);
                mRecyclerView.setAdapter(mTopicAdapter);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            progressWheel.setVisibility(View.INVISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };
    void paginate(int count) {

        if(!Util.isOnline(getActivity()))
            return;

        if(mLoading)
            return;

        mLoading = true;
        Requests.get("/posts/" + PreferenceManager.getInstance().getUserID() + "/posts/following/" + count,
                paginateResponseHandler);
    }
    private TextHttpResponseHandler paginateResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                JSONArray data = new JSONObject(responseString).getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    Post post = Post.from(data.getJSONObject(i));
                    mPostList.add(post);
                }
                int count = mTopicAdapter.getItemCount();
                mTopicAdapter.notifyItemRangeChanged(count, (mPostList.size() - 1));
            }catch (JSONException je) {}
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mLoading = false;
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onStart() {
            super.onStart();
            mSwipeRefreshLayout.setRefreshing(true);
        }
    };
}
