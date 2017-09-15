package com.beem24.projects.devafrica.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.beem24.projects.devafrica.entities.BookMark;
import com.beem24.projects.devafrica.entities.Follower;

/**
 * Created By Adigun Hammed Olalekan
 * 7/7/2017.
 * Beem24, Inc
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "d________ppp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BookMark.CREATE_TABLE);
        db.execSQL(Follower.CREATE_FOLLOWER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE " + BookMark.BOOKMARK_TABLE);
            db.execSQL("DROP TABLE " + Follower.FOLLOWER_TABLE_NAME);
        }
    }
}
