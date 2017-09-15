package com.beem24.projects.devafrica.entities;

import android.text.TextUtils;
import android.util.Log;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.local.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Adigun Hammed Olalekan
 * 7/5/2017.
 * Beem24, Inc
 */

public class Post {

    public String postID = "";
    public User user;
    public String postContent = "";
    public String timeCreated = "";
    public String postTitle = "";
    public List<Photo> photoList = new ArrayList<>();
    public List<Tag> mTags = new ArrayList<>();
    public boolean bookmarked = false;

    public int commentCount = 0;

    public boolean liked = false;

    public JSONObject raw;

    public Post() {

    }
    public static Post from(JSONObject jsonObject) throws JSONException{
        Post post = new Post();
        post.postID = jsonObject.getString("post_id");
        post.postContent = jsonObject.getString("post_content");
        post.postTitle = jsonObject.getString("post_title");
        post.timeCreated = jsonObject.getString("timestamp");
        post.user = User.from(jsonObject.getJSONObject("user"));
        post.commentCount = jsonObject.getInt("comment_count");

        JSONArray tagArray = jsonObject.getJSONArray("tags");
        for (int i = 0; i < tagArray.length(); i++) {
            Tag tag = Tag.from(tagArray.getJSONObject(i));
            post.mTags.add(tag);
        }
        post.raw = jsonObject;
        post.bookmarked = DatabaseManager.getInstance().postBookmarked(post);
        return post;
    }
    public String getPostDescription() {
        if(postContent == null || postContent.isEmpty())
            return "";

        String description = "";
        try {

            JSONObject jsonObject = new JSONObject(postContent);
            JSONArray jsonArray = jsonObject.getJSONArray("nodes");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject innerContent = jsonArray.getJSONObject(i);
                if(innerContent.getString("type").equalsIgnoreCase("INPUT")) {
                    description = innerContent.getJSONArray("content").getString(0);
                    break;
                }
            }
        }catch (JSONException je) {
            Log.d(DevAfrica.TAG, "ERROR", je);
        }
        description = description.length() > 200 ? description.substring(0, 200) + "..." : description;
        return Jsoup.parse(description).text();
    }
    public String getThumbURI() {
        if(postContent == null || postContent.isEmpty())
            return "";

        String URI = "";
        try {

            JSONObject jsonObject = new JSONObject(postContent);
            JSONArray jsonArray = jsonObject.getJSONArray("nodes");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject innerContent = jsonArray.getJSONObject(i);
                //Log.d(DevAfrica.TAG, innerContent.toString());
                if(innerContent.getString("type").equalsIgnoreCase("img")) {
                    URI = innerContent.getJSONArray("content").getString(0);
                    break;
                }
            }
        }catch (JSONException je) {
            Log.d(DevAfrica.TAG, "ERROR", je);
        }
        return URI.trim();
    }
    public String rawPost() {
        return raw.toString();
    }
    public boolean hasPhoto() {
        return !TextUtils.isEmpty(getThumbURI());
    }
}
