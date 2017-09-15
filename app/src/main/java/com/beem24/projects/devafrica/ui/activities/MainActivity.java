package com.beem24.projects.devafrica.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.services.FcmTokenListener;
import com.beem24.projects.devafrica.ui.adapters.TabRecyclerViewAdapter;
import com.beem24.projects.devafrica.ui.fragments.BookMarkFragment;
import com.beem24.projects.devafrica.ui.fragments.ExploreFragment;
import com.beem24.projects.devafrica.ui.fragments.FollowingFragment;
import com.beem24.projects.devafrica.ui.fragments.NotificationFragment;
import com.beem24.projects.devafrica.ui.fragments.ProfileFragment;
import com.beem24.projects.devafrica.ui.views.BadgeTabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created By Adigun Hammed Olalekan
 * 7/5/2017.
 * Beem24, Inc
 */

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    @BindView(R.id.tab_layout_main)
    BadgeTabLayout mTabLayout;
    @BindView(R.id.view_pager_main)
    ViewPager mViewPager;
    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.fab_new_post)
    FloatingActionButton mFab;

    public static final int NOTIFICATION_TAB_INDEX = 3;


    private List<Fragment> mFragments = new ArrayList<>();

    private TabAdapter tabAdapter;

    private MenuItem mSettingsMenuItem;

    public static Intent notificationTabIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("_scroll_to", NOTIFICATION_TAB_INDEX);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(mToolbar);


        mFragments.add(ExploreFragment.newInstance());
        mFragments.add(FollowingFragment.newInstance());
        mFragments.add(NotificationFragment.newInstance());
        mFragments.add(new BookMarkFragment());
        mFragments.add(ProfileFragment.newInstance());
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(tabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);


        mTabLayout.with(0).icon(R.drawable.ic_explore_black_24dp).build();
        mTabLayout.with(1).icon(R.drawable.ic_person_pin_black_24dp).build();
        BadgeTabLayout.Builder builder = mTabLayout.with(2).icon(R.drawable.ic_notifications_active_black_24dp).badgeCount(10)
                .withBadge();
        builder.build();
        mTabLayout.with(3).icon(R.drawable.ic_bookmark_black_24dp).build();
        mTabLayout.with(4).icon(R.drawable.ic_account_circle_black_24dp).build();

        builder.decrease();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("DevAfrica");
        }
        mViewPager.setOffscreenPageLimit(5);

        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        if(!PreferenceManager.getInstance().hasSavedToken()
                && PreferenceManager.getInstance().hasSession() && fcmToken != null) {
            saveFcmToken(fcmToken);
        }
        registerReceiver(broadcastReceiver, new IntentFilter(FcmTokenListener.ACTION_TOKEN_REFRESH));
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                String token = intent.getStringExtra("token_");

                if(PreferenceManager.getInstance().hasSession() && !PreferenceManager.getInstance().hasSavedToken())
                    saveFcmToken(token);
            }
        }
    };
    void saveFcmToken(final String token) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("fcm_id", token);

        Requests.put("/user/" + PreferenceManager.getInstance().getUserID() + "/fcm/create", requestParams,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        saveFcmToken(token);

                        Log.d(DevAfrica.TAG, responseString + "ERROR", throwable);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        PreferenceManager.getInstance().saveFCMToken(token);

                        Log.d(DevAfrica.TAG, responseString + "_");
                    }
                });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position >= 1) {
            mFab.hide();
        }else {
            mFab.show();
        }
        if(mSettingsMenuItem != null) {
            if(position == 4) {
                mSettingsMenuItem.setVisible(true);
            }else {
                mSettingsMenuItem.setVisible(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class TabAdapter extends FragmentPagerAdapter {

        public TabAdapter(FragmentManager fm) {
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
    }
    @OnClick(R.id.fab_new_post) public void onNewPost() {
        Intent intent = new Intent(this, ActivityNewTopic.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        mSettingsMenuItem = menu.findItem(R.id.menu_item_settings);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("query_", "***********");
                startActivity(intent);
                break;
            case R.id.menu_item_settings:
                Intent in = new Intent(this, SettingsActivity.class);
                startActivity(in);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(FcmTokenListener.ACTION_TOKEN_REFRESH));
    }
}
