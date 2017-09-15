package com.beem24.projects.devafrica.core;

import android.text.TextUtils;
import android.util.Log;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.entities.Queue;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import cz.msebera.android.httpclient.Header;

/**
 * Created By Adigun Hammed Olalekan
 * 7/11/2017.
 * Beem24, Inc
 */

public class RequestQueue implements Runnable{

    /*
    * A queue of requests. Created to confiniently push multiple requests to a backend server.
    *
    * e.g - Following a user is a full HTTP request - following 30 users at time leads to 30 HTTP requests which is equivalent to
    * 30 worker thread. This can be expensive on Android and could cause the App to crash with OOM - OutOfMemoryError.
    *
    * This queue accept unlimited number of request and process them one after the other. It uses a technique called POISON_PILL to pass a
    * shut down notification to the single worker thread, typically when the system call android.app.Application.onTerminate()
    * */

    private BlockingQueue<Queue> mBlockingQueue = new LinkedBlockingQueue<>();
    public static final String POISON_PILL = "_poison_";

    //singleton instance.
    private static RequestQueue mRequestQueue;

    //syncronous Http client since we are using a backgroud Thread.
    private AsyncHttpClient asyncHttpClient = new SyncHttpClient();
    private volatile boolean isRunning = true;

    public static synchronized RequestQueue getRequestQueue() {
        if(mRequestQueue == null) mRequestQueue = new RequestQueue();
        return mRequestQueue;
    }
    private RequestQueue() {
        String token = PreferenceManager.getInstance().getToken();
        asyncHttpClient.addHeader("Authorization", token);
    }

    @Override
    public void run() {

        //continue processing the queue until a shutdown notification is passed.
        while (true) {
            try {

                //take the next request
                Queue queue = mBlockingQueue.take();
                if(TextUtils.equals(queue.mURL, POISON_PILL)) {
                    //Poison pill...break it up and die!
                    break;
                }
                //post the request.
                asyncHttpClient.post(queue.mURL, queue.mRequestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    }
                });
            }catch (Exception e) {break;}
        }
    }
    public void add(Queue queue) {

        if(!isRunning) {
            //already shut down - this seem impossible.
            return;
        }
        try {
            mBlockingQueue.put(queue);
        }catch (InterruptedException e) {}
    }
    public void shutDown() {
        if(!isRunning)
            return;


        isRunning = false;
        Queue queue = new Queue();
        queue.mURL = POISON_PILL;
        try {
            mBlockingQueue.put(queue);
        }catch (Exception e) {}
    }
}
