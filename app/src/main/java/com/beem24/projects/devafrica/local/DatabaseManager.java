package com.beem24.projects.devafrica.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.entities.BookMark;
import com.beem24.projects.devafrica.entities.Follower;
import com.beem24.projects.devafrica.entities.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created By Adigun Hammed Olalekan
 * 7/7/2017.
 * Beem24, Inc
 */

public class DatabaseManager {

    private static DatabaseManager sInstance;
    private DatabaseHelper databaseHelper;

    public static synchronized DatabaseManager getInstance() {
        if(sInstance == null) sInstance = new DatabaseManager();return sInstance;
    }
    private DatabaseManager() {
        databaseHelper = new DatabaseHelper(DevAfrica.getApp().getApplicationContext());
    }

    public SQLiteDatabase getDB() {
        return databaseHelper.getWritableDatabase();
    }
    public void bookmark(BookMark bookMark) {
        if(postBookmarked(bookMark.getPost())) {
            deleteBookmark(bookMark);
        }else {
            getDB().insert(BookMark.BOOKMARK_TABLE, null, bookMark.getContentValues());
        }
    }
    public void deleteBookmark(BookMark bookMark) {
        getDB().delete(BookMark.BOOKMARK_TABLE, BookMark.COLUMN_REMOTE_ID + "=?", new String[]{String.valueOf(bookMark.mPostID)});

    }
    public boolean postBookmarked(Post post) {
        Cursor cursor = getDB().query(BookMark.BOOKMARK_TABLE, null, null, null, null, null, null);
        if(cursor == null)
            return false;
        if(cursor.getCount() <= 0 && !cursor.moveToFirst())
            return false;

        boolean exists = false;
        while (cursor.moveToNext()) {
            BookMark bookMark = BookMark.from(cursor);
            if(TextUtils.equals(bookMark.mPostID, post.postID)) {
                exists = true;
                break;
            }
        }
        return exists;
    }
    public List<Post> getBookmarked() {
        List<Post> posts = new ArrayList<>();
        Cursor cursor = getDB().query(BookMark.BOOKMARK_TABLE, null, null, null, null, null, null, null);
        if(cursor == null)
            return posts;
        if(cursor.getCount() <= 0 && !cursor.moveToFirst()) {
            return posts;
        }
        while (cursor.moveToNext()) {
            BookMark bookMark = BookMark.from(cursor);
            posts.add(bookMark.getPost());
        }
        return posts;
    }
    public void addFollower(Follower follower) {
        if(!isFollowing(follower)) {
            getDB().insert(Follower.FOLLOWER_TABLE_NAME, null, follower.getContentValues());
        }
    }
    public boolean isFollowing(Follower follower) {
        Cursor cursor = getDB().query(Follower.FOLLOWER_TABLE_NAME, null, Follower.COLUMN_REMOTE_ID + "=?",
                new String[]{String.valueOf(follower.ID)}, null, null, null);
        if(cursor == null)
            return false;
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
    public void removeFollower(Follower follower) {
        getDB().delete(Follower.FOLLOWER_TABLE_NAME, Follower.COLUMN_REMOTE_ID + "=?", new String[]{String.valueOf(follower.ID)});
    }
    public List<Follower> getFollowers() {
        Cursor cursor = getDB().query(Follower.FOLLOWER_TABLE_NAME, null, null, null, null, null, null);
        if(cursor == null)
            return Collections.emptyList();

        if(cursor.getCount() < 0 && !cursor.moveToFirst())
            return Collections.emptyList();

        List<Follower> followers = new ArrayList<>();
        while (cursor.moveToNext()) {
            Follower follower = Follower.from(cursor);
            followers.add(follower);
        }
        return followers;
    }
}
