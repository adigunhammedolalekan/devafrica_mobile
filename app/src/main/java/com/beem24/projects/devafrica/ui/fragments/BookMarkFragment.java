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
import com.beem24.projects.devafrica.entities.Post;
import com.beem24.projects.devafrica.local.DatabaseManager;
import com.beem24.projects.devafrica.ui.adapters.TopicAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/10/2017.
 * Beem24, Inc
 */

public class BookMarkFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.rv_bookmark_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_empty_bookmark)
    LinearLayout mLinearLayout;
    @BindView(R.id.swipe_layout_bookmark_fragment)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TopicAdapter topicAdapter;
    private List<Post> mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bookmarked_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mData = DatabaseManager.getInstance().getBookmarked();
        if(mData.size() <= 0) {
            mLinearLayout.setVisibility(View.VISIBLE);
            topicAdapter = new TopicAdapter(getActivity());
        }else {
            mLinearLayout.setVisibility(View.INVISIBLE);
            topicAdapter = new TopicAdapter(getActivity(), mData);
            mRecyclerView.setAdapter(topicAdapter);
        }

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent);
    }

    @Override
    public void onRefresh() {
        mData = DatabaseManager.getInstance().getBookmarked();
        if(mData.size() <= 0) {
            mLinearLayout.setVisibility(View.VISIBLE);
        }else {
            mLinearLayout.setVisibility(View.GONE);
            topicAdapter.notifyDataSetChanged();
        }
    }
}
