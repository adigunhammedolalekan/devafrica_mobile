package com.beem24.projects.devafrica.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.activities.SearchActivity;
import com.beem24.projects.devafrica.ui.adapters.FollowUserAdapter;
import com.beem24.projects.devafrica.ui.views.EndLessScrollListener;
import com.beem24.projects.devafrica.util.JSON;
import com.beem24.projects.devafrica.util.Util;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cz.msebera.android.httpclient.Header;

/**
 * Created By Adigun Hammed Olalekan
 * 7/10/2017.
 * Beem24, Inc
 */

public class DevSearchResult extends BaseFragment {

    @BindView(R.id.rv_dev_search_result)
    RecyclerView mRecyclerView;
    @BindView(R.id.pw_dev_search_result)
    ProgressWheel progressWheel;
    @BindView(R.id.swipe_layout_dev_search_result)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_dev_found_layout)
    LinearLayout mLinearLayout;

    private LinearLayoutManager mLinearLayoutManager;
    private volatile boolean mLoading = false;
    private List<User> users = new ArrayList<>();
    private FollowUserAdapter followUserAdapter;

    private String mQuery = "";

    public static DevSearchResult newInstance(String query) {

        Bundle args = new Bundle();
        args.putString("query_", query);

        DevSearchResult fragment = new DevSearchResult();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_dev_search_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((SearchActivity) getActivity()).addOnQuerySubmitListener(onQuerySubmittedListener);
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
            search(query);
            mQuery = query;
        }
    };
    void search(String query) {
        if(!Util.isOnline(getActivity())) {
            toast("Device is offline");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if(users != null) {
            users.clear();
            if(followUserAdapter != null)
                followUserAdapter.notifyDataSetChanged();
        }
        Requests.get("/user/" + query + "/search/0", textHttpResponseHandler);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            users = JSON.users(responseString);
            if(users.size() <= 0 ) {
                mLinearLayout.setVisibility(View.VISIBLE);
            }else {
                mLinearLayout.setVisibility(View.GONE);
                followUserAdapter = new FollowUserAdapter(getActivity(), users);
                mRecyclerView.setAdapter(followUserAdapter);
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
    void paginate(int count) {
        if(!Util.isOnline(getActivity())) {
            return;
        }
        if(mLoading)
            return;

        mLoading = true;
        Requests.get("/user/" + mQuery + "/search/" +count, pgHandler);
    }
    private TextHttpResponseHandler pgHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            List<User> userList = JSON.users(responseString);
            for (User u : userList) {
                users.add(u);
            }
            int count = followUserAdapter.getItemCount();
            followUserAdapter.notifyItemRangeChanged(count, (users.size() - 1));
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mLoading = false;
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onStart() {
            super.onStart();
            swipeRefreshLayout.setRefreshing(true);
        }
    };
}
