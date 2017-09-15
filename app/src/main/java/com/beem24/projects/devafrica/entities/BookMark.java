package com.beem24.projects.devafrica.entities;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created By Adigun Hammed Olalekan
 * 7/10/2017.
 * Beem24, Inc
 */

public class BookMark {

    public long ID = 0;
    public String JSON = "";
    public Post bookmarked;
    public String mPostID = "";

    public static final String BOOKMARK_TABLE = "bookmarks_";
    public static final String COLUMN_ID = "_id_";
    public static final String COLUMN_REMOTE_ID = "post_remote_id";
    public static final String COLUMN_DATA = "_data_";

    public static final String CREATE_TABLE = "create table " + BOOKMARK_TABLE + "(" + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_REMOTE_ID + " integer, " +
            COLUMN_DATA +" text);";

    public Post getPost() {
        try {
            return Post.from(new JSONObject(JSON));
        }catch (JSONException je) {}
        return new Post();
    }
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATA, JSON);
        contentValues.put(COLUMN_REMOTE_ID, mPostID);

        return contentValues;
    }
    public static BookMark from(Cursor cursor) {
        BookMark bookMark = new BookMark();
        bookMark.JSON = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATA));
        bookMark.mPostID = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMOTE_ID));
        bookMark.ID = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));

        return bookMark;
    }
}
