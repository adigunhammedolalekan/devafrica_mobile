package com.beem24.projects.devafrica.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.beem24.projects.devafrica.R;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 7/4/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private volatile boolean isClosed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }
    public void toast(String message) {
        if(isClosed)
            return;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public void showDialog(String title, String message) {
        if(isClosed)
            return;

        new AlertDialog.Builder(this, R.style.AlertDialogStyle).setTitle(title).setMessage(message)
                .setPositiveButton("OK", null).create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isClosed = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isClosed = true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
