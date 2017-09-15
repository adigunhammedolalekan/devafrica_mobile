package com.beem24.projects.devafrica.entities;

import com.beem24.projects.devafrica.local.DatabaseManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created By Adigun Hammed Olalekan
 * 7/5/2017.
 * Beem24, Inc
 */

public class User {

    public String ID = "";
    public String email = "";
    public String username = "";
    public String mPhoto = "";
    public String country = "";
    public int followerCount = 0;
    public String bio = "";
    public int mPostCount = 0;
    public int followingCount = 0;
    public String mStack = "";
    public String facebook = "";
    public String github = "";
    public String twitter = "";
    public String timeJoined = "";

    public boolean selected = false;

    public JSONObject raw;

    public String raw() {
        return raw.toString();
    }

    public User() {}

    public static User from(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.ID = jsonObject.getString("user_id");
        if(jsonObject.has("email")) {
            user.email = jsonObject.getString("email");
        }
        if(jsonObject.has("username")) {
            user.username = jsonObject.getString("username");
        }
        if(jsonObject.has("photo")) {
            user.mPhoto = jsonObject.getString("photo");
        }
        if(jsonObject.has("country")) {
            user.country = jsonObject.getString("country");
        }
        if(jsonObject.has("bio")) {
            user.bio = jsonObject.getString("bio");
        }
        if(jsonObject.has("post_count")) {
            user.mPostCount = jsonObject.getInt("post_count");
        }
        if(jsonObject.has("following_count")) {
            user.followingCount = jsonObject.getInt("following_count");
        }
        if(jsonObject.has("follower_count")) {
            user.followerCount = jsonObject.getInt("follower_count");
        }
        if(jsonObject.has("stack")) {
            user.mStack = jsonObject.getString("stack");
        }
        if(jsonObject.has("is_following")) {
            user.selected = jsonObject.getBoolean("is_following");
        }
        if(jsonObject.has("fb")) {
            user.facebook = jsonObject.getString("fb");
        }
        if(jsonObject.has("github")) {
            user.github = jsonObject.getString("github");
        }
        if(jsonObject.has("twitter")) {
            user.twitter = jsonObject.getString("twitter");
        }
        if(jsonObject.has("timestamp")) {
            user.timeJoined = jsonObject.getString("timestamp");
        }

        Follower follower = new Follower();
        follower.ID = Integer.parseInt(user.ID);
        follower.mUsername = user.username;

        user.selected = DatabaseManager.getInstance().isFollowing(follower);
        user.raw = jsonObject;
        return user;
    }
}
