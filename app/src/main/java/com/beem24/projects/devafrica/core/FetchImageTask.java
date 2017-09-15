package com.beem24.projects.devafrica.core;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import com.beem24.projects.devafrica.entities.Photo;
import com.beem24.projects.devafrica.interfaces.IFetchImageTaskListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 6/13/2017.
 */

public class FetchImageTask implements Runnable {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private IFetchImageTaskListener mImageFetchTaskListener;
    private Context mContext;
    private List<Photo> result = new ArrayList<>();
    public FetchImageTask(Context c, IFetchImageTaskListener fetchTaskListener) {
        mImageFetchTaskListener = fetchTaskListener;
        mContext = c;
    }
    @Override
    public void run() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = MediaStore.Images.Media.query(
                mContext.getContentResolver(), uri, projection
                , null, orderBy);
        if(cursor == null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mImageFetchTaskListener.onComplete(Collections.<Photo>emptyList());
                }
            });
            return;
        }
        final List<Photo> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            String next = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
            Photo photo = new Photo();
            photo.mPath = next;
            photo.isSelected = false;
            result.add(photo);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mImageFetchTaskListener.onComplete(result);
            }
        });

    }
}
