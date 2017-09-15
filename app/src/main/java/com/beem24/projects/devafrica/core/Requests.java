package com.beem24.projects.devafrica.core;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * 6/13/2017.
 */

public final class Requests {

    public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public static String BASE_URL = "http://www.growberng.com/devafrica/api";
    public static final String MEDIA_URI = "http://www.growberng.com/devafrica/";

    public static void post(String endPoint, RequestParams requestParams, TextHttpResponseHandler textHttpResponseHandler) {
        asyncHttpClient.addHeader("Authorization", PreferenceManager.getInstance().getToken());
        asyncHttpClient.post(BASE_URL + endPoint, requestParams, textHttpResponseHandler);
    }
    public static void get(String endPoint, RequestParams requestParams, TextHttpResponseHandler responseHandler) {
        asyncHttpClient.addHeader("Authorization", PreferenceManager.getInstance().getToken());
        asyncHttpClient.get(BASE_URL + endPoint, requestParams, responseHandler);
    }
    public static void get(String endPoint, TextHttpResponseHandler textHttpResponseHandler) {
        get(endPoint, null, textHttpResponseHandler);
    }
    public static void put(String endPoint, RequestParams requestParams, TextHttpResponseHandler textHttpResponseHandler) {
        asyncHttpClient.addHeader("Authorization", PreferenceManager.getInstance().getToken());
        asyncHttpClient.put(BASE_URL + endPoint, requestParams, textHttpResponseHandler);
    }
}
