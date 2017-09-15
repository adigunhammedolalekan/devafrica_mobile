package com.beem24.projects.devafrica.core;

import android.util.Log;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by root on 8/8/17.
 */

public class FcmNotificationSender {

    private MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String ItsNotASecret = "";
    private JSONObject jsonObject;

    private static final String URL = "https://fcm.googleapis.com/fcm/send";

    private String username = "";
    private String photo = "";
    private String userID = "";
    private String message = "";
    private String to = "";

    private FcmNotificationSender(String name, String id, String photo, String message,
                                  String to) {
        username = name;
        this.photo = photo;
        this.userID = id;
        this.message = message;
        this.to = to;
        this.ItsNotASecret = DevAfrica.getApp().getApplicationContext()
                .getString(R.string.okay_you_don_t_need_it);
    }

    public static class Builder {

        private String username = "";
        private String userPhoto = "";
        private String userID = "";
        private String message = "";
        private String to = "";

        public Builder username(String name) {
            username = name;
            return this;
        }
        public Builder to(String to) {
            this.to = to;
            return this;
        }
        public Builder photo(String uri) {
            userPhoto = uri;
            return this;
        }
        public Builder userID(String id) {
            userID = id;
            return this;
        }
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public FcmNotificationSender build() {
            return new FcmNotificationSender(username, userID, userPhoto, message, to);
        }
    }
    public void send() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(mediaType, getJsonBody());
        }catch (JSONException je) {}
        Request request = new Request.Builder()
                .addHeader("Authorization", "key="+ItsNotASecret)
                .addHeader("content-type", "application/json").url(URL)
                .post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(DevAfrica.TAG, "ERROR", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(DevAfrica.TAG, response.body().string() + "_");
            }
        });
    }
    private String getJsonBody() throws JSONException{

        JSONObject root = new JSONObject();
        root.put("to", to);
        JSONObject data = new JSONObject();
        data.put("user_id", userID);
        data.put("username", username);
        data.put("photo", photo);
        data.put("message", message);
        root.put("data", data);

        return root.toString();
    }
}
