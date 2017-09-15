package com.beem24.projects.devafrica.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created By Adigun Hammed Olalekan
 * 7/8/2017.
 * Beem24, Inc
 */

public class Comment {

    public String mCommentID = "";
    public String mPostID = "";
    public User mUser;
    public String commentBody = "";
    public boolean isReply = false;
    public int replyCount = 0;
    public String timePosted = "";
    public String postTitle = "";

    private JSONObject raw;

    public String raw() {
        return raw.toString();
    }


    public static Comment from(JSONObject jsonObject) throws JSONException {
        Comment comment = new Comment();
        comment.mPostID = jsonObject.getString("post_id");
        comment.commentBody = jsonObject.getString("comment_body");
        comment.mUser = User.from(jsonObject.getJSONObject("user"));
        comment.timePosted = jsonObject.getString("timestamp");
        comment.mCommentID = jsonObject.getString("comment_id");
        comment.replyCount = jsonObject.getInt("reply_count");
        JSONObject post = jsonObject.getJSONObject("post");
        comment.postTitle = post.getString("post_title");

        comment.raw = jsonObject;
        return comment;
    }
    public static Comment fromReply(JSONObject jsonObject) throws JSONException {
        Comment comment = new Comment();
        comment.commentBody = jsonObject.getString("content");
        comment.timePosted = jsonObject.getString("timestamp");
        comment.mCommentID = jsonObject.getString("comment_id");
        comment.mUser = User.from(jsonObject.getJSONObject("user"));

        return comment;
    }
}
