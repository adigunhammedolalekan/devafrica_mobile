package com.beem24.projects.devafrica.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Post;
import com.beem24.projects.devafrica.ui.activities.SearchActivity;
import com.beem24.projects.devafrica.ui.adapters.TopicAdapter;
import com.beem24.projects.devafrica.ui.views.EndLessScrollListener;
import com.beem24.projects.devafrica.util.JSON;
import com.beem24.projects.devafrica.util.Util;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import butterknife.BindView;
import cz.msebera.android.httpclient.Header;

/**
 * Created By Adigun Hammed Olalekan
 * 7/10/2017.
 * Beem24, Inc
 */

public class PostSearchResult extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.rv_post_search_result)
    RecyclerView mRecyclerView;
    @BindView(R.id.pw_post_search_result)
    ProgressWheel progressWheel;
    @BindView(R.id.swipe_layout_post_search_result)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_post_found_layout_post_search_result)
    LinearLayout noPost;

    TopicAdapter topicAdapter;
    private List<Post> posts;

    String mQuery = "";
    private volatile boolean mLoading = false;
    private LinearLayoutManager mLinearLayoutManager;

    public static PostSearchResult newInstance(String query) {

        Bundle args = new Bundle();
        args.putString("query_", query);

        PostSearchResult fragment = new PostSearchResult();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_post_search_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((SearchActivity) getActivity()).addOnQuerySubmitListener(onQuerySubmittedListener);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.addOnScrollListener(new EndLessScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                paginate(totalItemsCount);
            }
        });
    }

    private SearchActivity.OnQuerySubmittedListener onQuerySubmittedListener = new SearchActivity.OnQuerySubmittedListener() {
        @Override
        public void onSubmit(String query) {
            mQuery = query;
            search(query);
        }
    };
    void search(String query) {
        if(posts != null) {
            posts.clear();
        }
        if(topicAdapter != null)
            topicAdapter.notifyDataSetChanged();
        Requests.get("/posts/" + query + "/search/0", textHttpResponseHandler);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("Error occurred. Please retry.");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            posts = JSON.posts(responseString);
            if(posts.size() <= 0) {
                noPost.setVisibility(View.VISIBLE);
                topicAdapter = new TopicAdapter(getActivity(), posts);
                mRecyclerView.setAdapter(topicAdapter);
            }else {
                noPost.setVisibility(View.GONE);
                topicAdapter = new TopicAdapter(getActivity(), posts);
                mRecyclerView.setAdapter(topicAdapter);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            progressWheel.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onStart() {
            super.onStart();
            progressWheel.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onRefresh() {
        if(!Util.isOnline(getActivity())) {
            toast("Device is offline!");
            return;
        }
        search(mQuery);
    }
    void paginate(int count) {
        if(!Util.isOnline(getActivity()))
            return;

        if(mLoading) {
            return;
        }
        mLoading = true;
        Requests.get("/posts/" + mQuery + "/search/" + count, paginateResponseHandler);
    }
    TextHttpResponseHandler paginateResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            List<Post> postList = JSON.posts(responseString);

            for (Post post : postList)
                posts.add(post);

            int count = topicAdapter.getItemCount();
            topicAdapter.notifyItemRangeChanged(count, (posts.size() - 1));
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
