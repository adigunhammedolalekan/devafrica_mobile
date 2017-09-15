package com.beem24.projects.devafrica.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 6/13/2017.
 */

public class Util {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd_mm_yyyy_hh_mm_ss", Locale.ENGLISH);
    public static SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    public static boolean isOnline(Context context) {
        if(context == null)
            return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
    public static File getPhotoFile(Context context) {
        if(context == null) {
            return null;
        }
        File StorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(StorageDir == null)
            StorageDir = Environment.getExternalStorageDirectory();
        String storageDIR = StorageDir.getAbsolutePath() + "/OgaTechi";
        File file = new File(storageDIR);
        if(!file.exists())
            file.mkdirs();

        File photoFile = new File(file, "IMG_"+SIMPLE_DATE_FORMAT.format(new Date()) + ".jpg");
        return photoFile;
    }
    public static boolean empty(MaterialEditText materialEditText) {
        return materialEditText.getText().toString().trim().length() <= 0;
    }
    public static String text(MaterialEditText editText) {
        return editText.getText().toString().trim();
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static File getCompressedFile(String path, Context context) throws IOException {
        if(context == null)
            return new File(path);
        if(path == null) {
            return null;
        }
        File fresh = new File(path);
        long size = fresh.length() / 1000; //To KB

        //doesn't need compression. Use as it is.
        if(Math.abs(size) <= 400) {
            return fresh;
        }


        File cacheDIR = context.getExternalCacheDir();
        if(cacheDIR == null)
            cacheDIR = context.getCacheDir();
        String tempCacheDIR = cacheDIR.getAbsolutePath() + "/DevAfrica/TempC";
        File tempCacheFile = new File(tempCacheDIR);
        if(!tempCacheFile.exists())
            tempCacheFile.mkdirs();

        Bitmap bitmap = BitmapUtils.decodeImageFromFiles(path, 300, 300);
        if(bitmap == null) {
            //are you kidding me?
            return null;
        }
        File mainFile = new File(tempCacheFile, SIMPLE_DATE_FORMAT.format(new Date()) + ".jpg");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(mainFile);
        fileOutputStream.write(outputStream.toByteArray());
        fileOutputStream.flush();

        fileOutputStream.close();

        return mainFile;
    }
    public static String timeToString(String data) {
        try {
            long dateMillis = Long.valueOf(data);
            return sdf.format(new Date(dateMillis));
        }catch (NumberFormatException num) {return "";}
    }
    public static void startActivity(Context context, Class<?> className) {
        Intent intent = new Intent(context, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

