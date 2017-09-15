package com.beem24.projects.devafrica.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Post;
import com.beem24.projects.devafrica.ui.activities.NewTopicActivity;
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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created By Adigun Hammed Olalekan
 * 7/5/2017.
 * Beem24, Inc
 */

public class ExploreFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.swipe_layout_explore_fragment)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_explore_fragment)
    RecyclerView mRecyclerView;
    @BindView(R.id.pw_explore_fragment)
    ProgressWheel progressWheel;

    private volatile boolean mLoading = false;

    private List<Post> mPostList = new ArrayList<>();
    private TopicAdapter mTopicAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private EndLessScrollListener endLessScrollListener;
    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_explore, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(Util.isOnline(getActivity())) {
            callApi();
        }
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mTopicAdapter = new TopicAdapter(getActivity(), mPostList);

        endLessScrollListener = new EndLessScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                paginate(totalItemsCount);
            }
        };
        mRecyclerView.addOnScrollListener(endLessScrollListener);
    }
    void callApi() {
        Requests.get("/posts", textHttpResponseHandler);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            try {
                JSONObject jsonObject = new JSONObject(responseString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                mPostList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jb = jsonArray.getJSONObject(i);
                    Post post = Post.from(jb);
                    mPostList.add(post);
                }
                endLessScrollListener.resetState();
                mTopicAdapter = new TopicAdapter(getActivity(), mPostList);
                mRecyclerView.setAdapter(mTopicAdapter);
            }catch (JSONException je) {}
        }
        @Override
        public void onFinish() {
            super.onFinish();
            swipeRefreshLayout.setRefreshing(false);
            progressWheel.setVisibility(View.GONE);
        }
    };

    @Override
    public void onRefresh() {
        if(!Util.isOnline(getActivity())) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        callApi();
    }
    void paginate(int count) {
        if(!Util.isOnline(getActivity()))
            return;

        if(mLoading)
            return;

        mLoading = true;
        Requests.get("/posts/paginate/" + count, paginateResponseHandler);
    }
    private TextHttpResponseHandler paginateResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            List<Post> posts = JSON.posts(responseString);
            if(posts.size() <= 0) {
                return;
            }

            int totalItems = mTopicAdapter.getItemCount();
            mTopicAdapter.add(posts, totalItems);
        }

        @Override
        public void onStart() {
            super.onStart();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            swipeRefreshLayout.setRefreshing(false);
            mLoading = false;
        }
    };
}
