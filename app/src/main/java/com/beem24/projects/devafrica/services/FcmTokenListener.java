package com.beem24.projects.devafrica.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created By Adigun Hammed Olalekan
 * 7/25/2017.
 * Beem24, Inc
 */

public class FcmTokenListener extends FirebaseInstanceIdService {

    private String mToken = "";
    AsyncHttpClient asyncHttpClient = new SyncHttpClient();
    int retryCount = 0;

    public static final String ACTION_TOKEN_REFRESH = "com.beem24.projects.devafrica.ACTION_TOKEN_REFRESH";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        mToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(DevAfrica.TAG, "FCM ===> " + mToken);
        Intent intent = new Intent(ACTION_TOKEN_REFRESH);
        intent.putExtra("token_", mToken);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);

        if(PreferenceManager.getInstance().hasSession()) {
            save(mToken);
        }
    }

    void save(String token) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("fcm_id", token);

        asyncHttpClient.put("/user/" + PreferenceManager.getInstance().getUserID() + "/fcm/create", requestParams,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        //retry 5 times
                        if(retryCount < 6)
                            save(mToken);

                        retryCount++;
                        //retry - bounded recursion

                        Log.d(DevAfrica.TAG, "ERROR" + responseString, throwable);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        PreferenceManager.getInstance().saveFCMToken(mToken);
                        Log.d(DevAfrica.TAG, responseString + "_");
                    }
                });
    }
}
