package com.beem24.projects.devafrica.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import com.beem24.projects.devafrica.interfaces.IImageCompressionTaskListener;
import com.beem24.projects.devafrica.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 6/14/2017.
 */

public class ImageCompressionTask implements Runnable{

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private IImageCompressionTaskListener iImageCompressionTaskListener;
    private File mCacheLocation;
    private int mID;
    public List<String> mutlipleToCompress;
    private Context mContext;
    private List<File> compressed = new ArrayList<>();

    public ImageCompressionTask(Context context, IImageCompressionTaskListener taskListener,
                                List<String> source, int id) {
        if(context == null)
            throw new NullPointerException("Context == null");

        mContext = context;
        iImageCompressionTaskListener = taskListener;
        mID = id;
        File file = context.getExternalCacheDir();
        if(file == null) //failed to get external storage directory, fall back to internal cache directory
            file = context.getCacheDir();

        String location = file.getAbsolutePath() + "/DevAfrica";
        mCacheLocation = new File(location);
        if(!mCacheLocation.exists())
            mCacheLocation.mkdirs();
        mutlipleToCompress = source;
    }

    @Override
    public void run() {

        try{
            for (String path : mutlipleToCompress) {
                File file = Util.getCompressedFile(path, mContext);
                compressed.add(file);
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    iImageCompressionTaskListener.onCompressed(compressed, mID);
                }
            });
        } catch (final FileNotFoundException e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    iImageCompressionTaskListener.onError(e);
                }
            });
        }catch (final IOException io){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    iImageCompressionTaskListener.onError(io);
                }
            });}

    }
}
