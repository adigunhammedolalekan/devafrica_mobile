package com.beem24.projects.devafrica.entities;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created By Adigun Hammed Olalekan
 * 7/12/2017.
 * Beem24, Inc
 */

public class Follower {

    public int ID = 0;
    public String mUsername = "";

    public static final String FOLLOWER_TABLE_NAME = "_followers_";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_REMOTE_ID = "r_id";
    public static final String COLUMN_USERNAME = "_username_";

    public static final String CREATE_FOLLOWER_TABLE = "create Table " +FOLLOWER_TABLE_NAME+ " ( " + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_REMOTE_ID + " integer, " +
            COLUMN_USERNAME +" text );";

    public static Follower from(Cursor cursor) {
        Follower follower = new Follower();
        follower.ID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMOTE_ID));
        follower.mUsername = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));

        return follower;
    }
    public Follower() {}

    public Follower(String name) {
        mUsername = name;
    }
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_REMOTE_ID, ID);
        contentValues.put(COLUMN_USERNAME, mUsername);

        return contentValues;
    }
}
