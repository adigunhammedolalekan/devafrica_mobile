package com.beem24.projects.devafrica.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.Comment;
import com.beem24.projects.devafrica.entities.Notification;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Adigun Hammed Olalekan
 * 7/25/2017.
 * Beem24, Inc
 */

public class NotificationService extends FirebaseMessagingService {

    public static final int NEW_COMMENT_NOTIFICATION_ID = 1;
    public static final int NEW_FOLLOWER_NOTIFICATION_ID = 2;
    public static final int NEW_REPLY_NOTIFICATION_ID = 3;

    private NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage == null)
            return;

        if(remoteMessage.getData().size() > 0) {
            String JSON = remoteMessage.getData().toString();
            Log.d(DevAfrica.TAG, JSON + "_");

            handleNotification(JSON);

        }
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    void handleNotification(String notification) {

        List<Notification> notifications = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(notification);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject jb = data.getJSONObject(i);
                int type = jb.getInt("type");
                Notification notif = new Notification(type);
                notif.setData(jb);
                notifications.add(notif);
            }
        }catch (JSONException je) {
        }
        if(notifications.size() > 0)
            handleNotification(notifications);
    }
    void handleNotification(List<Notification> notifications) {

        for (Notification notification : notifications) {
            switch (notification.getType()) {
                case Notification.NEW_COMMENT:
                    List<Comment> comments = notification.getCommentList();
                    notifyNewComment(comments);
                    break;
                case Notification.NEW_FOLLOWER:
                    List<User> users = notification.getFollowerList();
                    notifyNewFollowers(users);
                    break;
                case Notification.NEW_REPLY:
                    List<Comment> replies = notification.getRepliesList();
                    notifyNewReplies(replies);
                    break;
                default:break;
            }
        }
    }
    void notifyNewComment(List<Comment> comments) {

        for (int i = 0; i < comments.size(); i++) {
            if(i == 10)
                break;

            Comment comment = comments.get(i);
            sendNotification(comment);
        }

    }
    void notifyNewFollowers(List<User> users) {

        for (int i = 0; i < users.size(); i++) {
            if(i == 10)
                break;

            User user = users.get(i);
            sendNotification(user);
        }
    }
    void notifyNewReplies(List<Comment> comments) {

        for (int i = 0; i < comments.size(); i++) {
            if(i == 10)
                break;

            Comment comment = comments.get(i);
            sendNotification(comment, NEW_REPLY_NOTIFICATION_ID);
        }
    }

    void sendNotification(Comment comment) {

        Intent intent = MainActivity.notificationTabIntent(this);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String commentStriped = Jsoup.parse(comment.commentBody).text();
        commentStriped = commentStriped.length() > 150 ? commentStriped.substring(0, 145) + "..." : commentStriped;
        String contentText = comment.mUser.username + " responded to your post - " + commentStriped;

        inboxStyle.addLine(commentStriped);
        android.app.Notification notification = notifBuilder.setStyle(inboxStyle)
                .setTicker("DevAfrica")
                .setContentText(contentText)
                .setContentTitle("DevAfrica")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_account_circle_black_24dp).setAutoCancel(true)
                .setWhen(0).setWhen(System.currentTimeMillis())
                .build();

        mNotificationManager.notify(NEW_COMMENT_NOTIFICATION_ID, notification);
    }
    void sendNotification(User user) {

        Intent intent = MainActivity.notificationTabIntent(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(user.username + " started following you.");
        android.app.Notification notification =
                notifBuilder.setContentIntent(pendingIntent).setAutoCancel(true)
                .setContentText(user.username + " started following you.")
                .setWhen(System.currentTimeMillis()).setTicker("DevAfrica")
                .setStyle(inboxStyle).build();

        mNotificationManager.notify(NEW_FOLLOWER_NOTIFICATION_ID, notification);
    }
    void sendNotification(Comment comment, int id) {

        String commentBody = Jsoup.parse(comment.commentBody).text();
        commentBody = commentBody.length() > 150 ? commentBody.substring(0, 145) + "..." : commentBody;
        String contentText = comment.mUser.username + " replied to your post - " +commentBody;

        Intent intent = MainActivity.notificationTabIntent(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(this);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(contentText);
        android.app.Notification notification = builder.setStyle(inboxStyle).setTicker("DevAfrica")
                .setAutoCancel(true).setContentIntent(pendingIntent).setContentTitle("DevAfrica").setContentText(contentText)
                .setWhen(System.currentTimeMillis()).build();

        mNotificationManager.notify(id, notification);
    }
}
