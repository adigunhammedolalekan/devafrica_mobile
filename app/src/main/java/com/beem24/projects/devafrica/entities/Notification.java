package com.beem24.projects.devafrica.entities;

import android.util.Log;

import com.beem24.projects.devafrica.DevAfrica;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Adigun Hammed Olalekan
 * 7/18/2017.
 * Beem24, Inc
 */

public class Notification {

    public static final int NEW_COMMENT = 1;
    public static final int NEW_FOLLOWER = 2;
    public static final int NEW_REPLY = 3;
    public static final int MENTION = 4;

    public String ID = "";
    public int mType = 0;

    public List<Comment> commentList = new ArrayList<>();
    public List<User> followerList = new ArrayList<>();
    public List<Comment> repliesList = new ArrayList<>();

    private Comment comment = new Comment();
    private User follower = new User();
    private Comment reply = new Comment();
    private JSONObject data;

    public Notification(int type, JSONObject isOfUi) {
       this.mType = type;
        this.data = isOfUi;
    }

    public Notification(int type) throws JSONException {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public void setData(JSONObject jsonObject) throws JSONException{

        switch (mType) {
            case NEW_COMMENT:
                JSONArray data = jsonObject.getJSONArray("comments");
                for (int i = 0; i < data.length(); i++) {
                    Comment comment = Comment.from(data.getJSONObject(i));
                    commentList.add(comment);
                }
                break;
            case NEW_FOLLOWER:
                JSONArray followers = jsonObject.getJSONArray("followers");
                for (int i = 0; i < followers.length(); i++) {
                    User user = User.from(followers.getJSONObject(i));
                    followerList.add(user);
                }
                break;
            case NEW_REPLY:
                JSONArray replies = jsonObject.getJSONArray("replies");
                for (int i = 0; i < replies.length(); i++) {
                    Comment comment = Comment.fromReply(replies.getJSONObject(i));
                    repliesList.add(comment);
                }
                break;
            case MENTION:
                break;
        }
    }

    public void setNotification() {
        try {
            switch (mType) {
                case NEW_COMMENT:
                    this.comment = Comment.from(data);
                    break;
                case NEW_FOLLOWER:
                    this.follower = User.from(data);
                    break;
                case NEW_REPLY:
                    this.reply = Comment.fromReply(data);
                    break;
            }
        }catch (JSONException je) {
            Log.d(DevAfrica.TAG, "ERROR", je);
        }
    }
    public List<Comment> getCommentList() {
        return commentList;
    }

    public List<User> getFollowerList() {
        return followerList;
    }

    public List<Comment> getRepliesList() {
        return repliesList;
    }

    public Comment getComment() {
        return comment;
    }

    public User getFollower() {
        return follower;
    }

    public Comment getReply() {
        return reply;
    }
}
