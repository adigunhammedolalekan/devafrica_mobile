package com.beem24.projects.devafrica.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.ui.fragments.DevSearchResult;
import com.beem24.projects.devafrica.ui.fragments.PostSearchResult;
import com.beem24.projects.devafrica.ui.fragments.TagSearchResult;
import com.beem24.projects.devafrica.util.Util;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/10/2017.
 * Beem24, Inc
 */

public class SearchActivity extends BaseActivity {

    @BindView(R.id.search_material_search_view)
    MaterialSearchView mMaterialSearchView;
    @BindView(R.id.view_pager_search)
    ViewPager mViewPager;
    @BindView(R.id.toolbar_search)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout_search)
    TabLayout mTabLayout;

    String mQuery = "";

    private List<Fragment> mFragments = new ArrayList<>();

    private List<OnQuerySubmittedListener> onQuerySubmittedListeners = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        if(intent == null) {
            finish();
            return;
        }
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mTabLayout.setVisibility(View.GONE);
        mQuery = intent.getStringExtra("query_");
        mMaterialSearchView.setOnQueryTextListener(onQueryTextListener);
        mMaterialSearchView.setHintTextColor(ContextCompat.getColor(this, R.color.white));
        mMaterialSearchView.setOnSearchViewListener(searchViewListener);
        mMaterialSearchView.setBackIcon(getResources().getDrawable(R.drawable.ic_action_navigation_arrow_back_inverted));

        mFragments.add(PostSearchResult.newInstance(mQuery));
        mFragments.add(DevSearchResult.newInstance(mQuery));
        //mFragments.add(TagSearchResult.newInstance(mQuery));

        SearchTabAdapter searchTabAdapter = new SearchTabAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(searchTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
    private MaterialSearchView.OnQueryTextListener onQueryTextListener = new MaterialSearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(query.trim().length() <= 0) {
                return false;
            }
            Util.hideKeyboard(SearchActivity.this);
            mTabLayout.setVisibility(View.VISIBLE);
            for (OnQuerySubmittedListener onQuerySubmittedListener : onQuerySubmittedListeners) {
                if(onQuerySubmittedListener != null)
                    onQuerySubmittedListener.onSubmit(query.trim());
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };
    private MaterialSearchView.SearchViewListener searchViewListener = new MaterialSearchView.SearchViewListener() {
        @Override
        public void onSearchViewShown() {
            mTabLayout.setVisibility(View.GONE);
        }

        @Override
        public void onSearchViewClosed() {

        }
    };

    public interface OnQuerySubmittedListener {
        void onSubmit(String query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_activity, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_search_activity);
        mMaterialSearchView.setMenuItem(menuItem);
        mMaterialSearchView.showSearch();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    class SearchTabAdapter extends FragmentPagerAdapter {

        public SearchTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Posts";
                case 1:
                    return "Devs";
            }
            return "";
        }
    }
    public void addOnQuerySubmitListener(OnQuerySubmittedListener querySubmittedListener) {
        onQuerySubmittedListeners.add(querySubmittedListener);
    }
}
