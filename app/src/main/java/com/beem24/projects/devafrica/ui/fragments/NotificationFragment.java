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
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Notification;
import com.beem24.projects.devafrica.entities.NotificationGroup;
import com.beem24.projects.devafrica.ui.adapters.NotificationAdapter;
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
 * 7/18/2017.
 * Beem24, Inc
 */

public class NotificationFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.swipe_layout_notifications)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rv_notification_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_no_notification)
    LinearLayout noNotificationLinearLayout;
    @BindView(R.id.pw_notification_fragment)
    ProgressWheel progressWheel;

    private List<Notification> notifications = new ArrayList<>();

    private NotificationAdapter notificationAdapter;

    public static NotificationFragment newInstance() {

        Bundle args = new Bundle();

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_notification_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        getNotifications();
    }
    void getNotifications() {
        String user = PreferenceManager.getInstance().getUserID();
        Requests.get("/user/" + user + "/notifications", textHttpResponseHandler);
    }

    @Override
    public void onRefresh() {
        if(!Util.isOnline(getActivity())) {
            toast("Device is offline");
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        getNotifications();
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Log.d(DevAfrica.TAG, "__" + responseString);
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                notifications.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jb = jsonArray.getJSONObject(i);
                    int type = jb.getInt("type");
                    switch (type) {
                        case 1:
                            JSONArray comments = jb.getJSONArray("comments");
                            for (int j = 0; j < comments.length(); j++) {
                                JSONObject comment = comments.getJSONObject(j);
                                Notification notification = new Notification(type, comment);
                                notification.setNotification();
                                notifications.add(notification);
                            }
                            break;
                        case 2:
                            JSONArray followers = jb.getJSONArray("followers");
                            for (int k = 0; k < followers.length(); k++) {
                                JSONObject follower = followers.getJSONObject(k);
                                Notification notification = new Notification(type, follower);
                                notification.setNotification();
                                notifications.add(notification);
                            }
                            break;
                        case 3:
                            JSONArray replies = jb.getJSONArray("replies");
                            for (int l = 0; l < replies.length(); l++) {
                                JSONObject follower = replies.getJSONObject(l);
                                Notification notification = new Notification(type, follower);
                                notification.setNotification();
                                notifications.add(notification);
                            }
                            break;
                    }
                }
            }catch (JSONException je) {}
            if(notifications.size() > 0) {
                notificationAdapter = new NotificationAdapter(getActivity(), notifications);
                mRecyclerView.setAdapter(notificationAdapter);
                noNotificationLinearLayout.setVisibility(View.INVISIBLE);
            }else {
                noNotificationLinearLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mSwipeRefreshLayout.setRefreshing(false);
            progressWheel.setVisibility(View.INVISIBLE);
        }
    };
}
