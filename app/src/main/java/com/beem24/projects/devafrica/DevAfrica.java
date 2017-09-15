package com.beem24.projects.devafrica;

import android.app.Application;
import android.util.Log;

import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.RequestQueue;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Queue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created By Adigun Hammed Olalekan
 * 7/4/2017.
 * Beem24, Inc
 */

public class DevAfrica extends Application {

    private static DevAfrica app;
    public static final String TAG = DevAfrica.class.getSimpleName();
    public static ExecutorService mExecutorService = Executors.newCachedThreadPool();
    private RequestQueue mRequestQueue;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Lato-Medium.ttf")
                .setFontAttrId(R.attr.fontPath).build());
        mRequestQueue = RequestQueue.getRequestQueue();
        mExecutorService.execute(mRequestQueue);

        if(PreferenceManager.getInstance().hasSession()) {
            Requests.get("/user/" + PreferenceManager.getInstance().getUserID() + "/profile", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        PreferenceManager.getInstance().save(new JSONObject(responseString));
                    }catch (JSONException je) {}
                }
            });
        }
    }

    @Override
    public void onTerminate() {
        shutQueue();
        super.onTerminate();
    }

    public static DevAfrica getApp() {
        return app;
    }
    public void addJob(Queue queue) {
        mRequestQueue.add(queue);
    }
    public void shutQueue() {
        mRequestQueue.shutDown();
    }
}
