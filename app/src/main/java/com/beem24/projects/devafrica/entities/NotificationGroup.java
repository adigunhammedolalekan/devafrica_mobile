package com.beem24.projects.devafrica.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Adigun Hammed Olalekan
 * 7/18/2017.
 * Beem24, Inc
 */

public class NotificationGroup {

    public int ID = -1;
    public int mType = -1;

    public List<Comment> comments = new ArrayList<>();

    public User mUser;

    public NotificationGroup(Comment comment) {
        this.ID = Integer.parseInt(comment.mPostID.trim());
        mType = Notification.NEW_COMMENT;
        comments.add(comment);
    }

    public NotificationGroup(User user) {
        mUser = user;
        mType = Notification.NEW_FOLLOWER;
    }
    public int getID() {
        return ID;
    }
    public String getFollowerNotificationText() {
        return "<b>" + mUser.username + "</b> started following you.";
    }
    public int getType() {
        return mType;
    }
    public void add(Comment comment) {
        comments.add(comment);
    }
    public void add(User user) {

    }

    public String getCommentNotificationText() {
        int howMany = comments.size();
        String response = "";
        switch (howMany) {
            case 1:
                Comment comment = comments.get(0);
                response = "<b>" + comment.mUser.username + "</b> responded to your post.";
                break;
            case 2:
                for (int i = 0; i < comments.size(); i++) {
                    response = "<b>" + comments.get(i).mUser.username + "</b> ";
                    if(i != (comments.size() - 1)) {
                        response += "And";
                    }
                }
                response += " responded to your post.";
                break;
            case 3:
                for (int j = 0; j < comments.size(); j++) {
                    response += "<b>" + comments.get(j).mUser.username + "</b>";
                    if(j <= (comments.size() - 2)) {
                        response += ", ";
                    }else {
                        response += "And ";
                    }
                }
                response += " responded to your post.";
                break;
            default:
                Comment first = comments.get(0);
                Comment second = comments.get(1);
                response += "<b>" + first.mUser.username + "</b>, <b>" + second.mUser.username + "</b> And";

                response += comments.size() - 2 + " Others responded to your post.";
                break;
        }

        return response;
    }
}
