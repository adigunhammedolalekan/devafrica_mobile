package com.beem24.projects.devafrica.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.entities.Language;
import com.beem24.projects.devafrica.entities.Post;
import com.beem24.projects.devafrica.entities.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created By Adigun Hammed Olalekan
 * 7/9/2017.
 * Beem24, Inc
 */

public final class JSON {

    public static List<Post> posts(String JSON) {

        List<Post> posts = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            posts.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                Post post = Post.from(jsonArray.getJSONObject(i));
                posts.add(post);
            }
        }catch (JSONException je) {}
        return posts;
    }
    public static List<User> users(String JSON) {
        List<User> users = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(JSON);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                User user = User.from(jsonArray.getJSONObject(i));
                users.add(user);
            }
        }catch (JSONException je) {
            Log.d(DevAfrica.TAG, "ERROR", je);
        }
        return users;
    }
    public static List<Language> languages(Context context) {
        if(context == null)
            return Collections.emptyList();

        StringBuilder stringBuilder = new StringBuilder();
        List<Language> languages = new ArrayList<>();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("lang.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String JSON = stringBuilder.toString();
            JSONObject jsonObject = new JSONObject(JSON);
            JSONArray jsonArray = jsonObject.getJSONArray("itemListElement");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject lang = jsonArray.getJSONObject(i).getJSONObject("item");
                Language language = new Language(lang);
                languages.add(language);
            }
        }catch (JSONException | IOException e) {
            Log.d(DevAfrica.TAG, "ERROR", e);
        }
        return languages;
    }
}
