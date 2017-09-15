package com.beem24.projects.devafrica.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.ui.activities.SearchActivity;

/**
 * Created By Adigun Hammed Olalekan
 * 7/10/2017.
 * Beem24, Inc
 */

public class TagSearchResult extends BaseFragment {

    public static TagSearchResult newInstance(String query) {

        Bundle args = new Bundle();
        args.putString("query_", query);
        TagSearchResult fragment = new TagSearchResult();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_tag_search_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((SearchActivity) getActivity()).addOnQuerySubmitListener(onQuerySubmittedListener);
    }
    private SearchActivity.OnQuerySubmittedListener onQuerySubmittedListener = new SearchActivity.OnQuerySubmittedListener() {
        @Override
        public void onSubmit(String query) {

        }
    };
}
