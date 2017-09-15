package com.beem24.projects.devafrica.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.AppSettings;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.entities.User;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created By Adigun Hammed Olalekan
 * 7/26/2017.
 * Beem24, Inc
 */

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.notification_settings_switch)
    SwitchCompat mSwitchCompat;
    @BindView(R.id.tv_sign_out_text)
    TextView signOutText;

    AppSettings appSettings;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_activity_settings);
        appSettings = AppSettings.getInstance();
        mSwitchCompat.setChecked(appSettings.notificationEnabled());

        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appSettings.setEnableNotification(isChecked);
            }
        });
        User user = PreferenceManager.getInstance().getUser();
        signOutText.setText("currently logged in as " + user.username);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Settings");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.layout_enable_notification_settings_activity) public void onEnableNotificationClick() {
        mSwitchCompat.toggle();
    }
    @OnClick(R.id.layout_root_edit_profile_settings_activity) public void onEditProfileClick() {
        startActivity(new Intent(this, EditProfileActivity.class));
    }
    @OnClick(R.id.layout_sign_out_activity_settings) public void onSignOutClick() {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Sign Out?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.getInstance().logOut();
                        Intent intent = new Intent(SettingsActivity.this, EntryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton("CANCEL", null).create().show();
    }
}
