package com.beem24.projects.devafrica.entities;

import com.loopj.android.http.RequestParams;

/**
 * Created By Adigun Hammed Olalekan
 * 7/11/2017.
 * Beem24, Inc
 */

public class Queue {

    public Queue() {}

    public Queue(String url) {
        mURL = url;
    }

    public String mURL = "";
    public RequestParams mRequestParams = new RequestParams();

}
