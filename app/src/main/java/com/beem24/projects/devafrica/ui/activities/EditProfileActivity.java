package com.beem24.projects.devafrica.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.ImageCompressionTask;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.interfaces.IImageCompressionTaskListener;
import com.beem24.projects.devafrica.ui.views.EditTextDialog;
import com.beem24.projects.devafrica.util.BitmapUtils;
import com.bumptech.glide.Glide;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/26/2017.
 * Beem24, Inc
 */

public class EditProfileActivity extends BaseActivity {

    @BindView(R.id.iv_user_edit_profile)
    CircleImageView dp;
    @BindView(R.id.bio_tv_edit_profile)
    TextView bioTextView;
    @BindView(R.id.country_region_tv_edit_profile)
    TextView locationTextView;
    @BindView(R.id.tech_stack_tv_edit_profile)
    TextView stackTextView;
    @BindView(R.id.tv_fb_username_edit_account)
    TextView fbUsernameTextView;
    @BindView(R.id.tv_twitter_username_edit_profile)
    TextView twitterUsernameTextView;
    @BindView(R.id.tv_github_username_edit_account)
    TextView githubUsernameTextView;
    @BindView(R.id.loading_layout_edit_profile)
    RelativeLayout loadingLayout;

    private User currentUser;
    private PreferenceManager mPreferenceManager;

    private EditTextDialog editTextDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_activity_edit_profile);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Edit Profile");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPreferenceManager = PreferenceManager.getInstance();
        currentUser = mPreferenceManager.getUser();
        render(currentUser);
    }
    void render(User user) {
        if(!user.mPhoto.isEmpty()) {
            Glide.with(this)
                    .load(user.mPhoto)
                    .placeholder(R.color.divider)
                    .error(R.color.divider).into(dp);
        }
        if(!user.bio.trim().isEmpty())
            bioTextView.setText(user.bio);
        if(!user.country.trim().isEmpty())
            locationTextView.setText(user.country);
        if(!user.mStack.isEmpty())
            stackTextView.setText(user.mStack);
        if(!user.facebook.isEmpty())
            fbUsernameTextView.setText(user.facebook);
        if(!user.twitter.isEmpty())
            twitterUsernameTextView.setText(user.twitter);
        if(!user.github.isEmpty())
            githubUsernameTextView.setText(user.github);
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
    @OnClick(R.id.layout_edit_bio) public void onEditBio() {
        editTextDialog = new EditTextDialog(this, "Edit Bio", "short note about you", getData(currentUser.bio), textDialogListener
                , 1);
        editTextDialog.show();
    }
    @OnClick(R.id.layout_edit_tech_stack) public void onEditStack() {
        Intent intent = new Intent(this, ActivitySelectStack.class);
        startActivityForResult(intent, 1001);
    }
    @OnClick(R.id.layout_edit_country_region) public void editCountryRegionClick() {
        editTextDialog = new EditTextDialog(this, "Change Location", "Location", getData(currentUser.country) , textDialogListener, 2);
        editTextDialog.show();
    }
    @OnClick(R.id.layout_add_fb_account) public void onAddFaceBookAccount() {
        editTextDialog = new EditTextDialog(this, "Facebook", "www.facebook.com/foo", getData(currentUser.facebook) ,textDialogListener, 4);
        editTextDialog.show();
    }
    @OnClick(R.id.layout_add_github_account) public void onAddGithubAccount() {
        editTextDialog = new EditTextDialog(this, "Github", "www.github.com/foo", getData(currentUser.github) ,textDialogListener, 5);
        editTextDialog.show();
    }
    @OnClick(R.id.layout_add_twitter_account) public void onAddTwitterAccount() {
        editTextDialog = new EditTextDialog(this, "Twitter", "@fooBar",
                getData(currentUser.twitter),textDialogListener, 6);
        editTextDialog.show();
    }
    String getData(String s) {
        return s.isEmpty() ? null : s.trim();
    }
    private EditTextDialog.IEditTextDialogListener textDialogListener = new EditTextDialog.IEditTextDialogListener() {
        @Override
        public void onFinish(String text, int id) {
            RequestParams requestParams = new RequestParams();

            switch (id) {
                case 1:
                    requestParams.put("key", "bio");
                    requestParams.put("value", text.trim());
                    bioTextView.setText(text);
                    break;
                case 2:
                    requestParams.put("key", "country");
                    requestParams.put("value", text);
                    locationTextView.setText(text);
                    break;
                case 4:
                    requestParams.put("key", "fb");
                    requestParams.put("value", text);
                    fbUsernameTextView.setText(text);
                    break;
                case 5:
                    requestParams.put("key", "github");
                    requestParams.put("value", text);
                    githubUsernameTextView.setText(text);
                    break;
                case 6:
                    requestParams.put("key", "twitter");
                    requestParams.put("value", text);
                    twitterUsernameTextView.setText(text);
                    break;
            }
            Requests.put("/user/" + mPreferenceManager.getUserID() + "/profile/edit", requestParams,
                    textHttpResponseHandler);
        }

        @Override
        public void onCancel() {

        }
    };
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("Error occurred while creating a secure connection");

            Log.d(DevAfrica.TAG, "ERROR" + responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Log.d(DevAfrica.TAG, "__" + responseString);
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                mPreferenceManager.save(jsonObject);
                toast("Profile detail updated");
            }catch (JSONException e) {
                //Log.d(Dev)
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            loadingLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            loadingLayout.setVisibility(View.GONE);
        }
    };
    @OnClick(R.id.layout_change_dp_edit_profile) public void onChangeDP() {
        requestPermission();
    }
    @OnClick(R.id.iv_user_edit_profile) public void onDpClick() {
        requestPermission();
    }
    void requestPermission() {
        boolean granted = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(granted) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1009);
        }else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1010);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1010);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1010) {
            if(grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1009);
                }
            }else {
                showDialog("Permission Denied", "DevAfrica was not granted the permission to read storage.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1009:
                    Uri uri = data.getData();
                    String path = BitmapUtils.getPath(this, uri);
                    List<String> strings = new ArrayList<>();
                    strings.add(path);
                    DevAfrica.mExecutorService.execute(new ImageCompressionTask(this, iImageCompressionTaskListener,
                            strings, 900));
                    break;
                case 1001:
                    String languageStack = data.getStringExtra("_selected_");
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("key", "stack");
                    requestParams.put("value", languageStack);
                    Requests.put("/user/" + mPreferenceManager.getUserID() + "/profile/edit", requestParams, textHttpResponseHandler);
                    break;
            }
        }
    }
    private IImageCompressionTaskListener iImageCompressionTaskListener = new IImageCompressionTaskListener() {
        @Override
        public void onCompressed(List<File> file, int id) {
            if(file == null || file.size() <= 0)
                return;

            File data = file.get(0);
            Glide.with(EditProfileActivity.this)
                    .load(data).error(R.color.divider).placeholder(R.color.divider).dontAnimate()
                    .into(dp);
            try {
                RequestParams requestParams = new RequestParams();
                requestParams.put("dp", data);
                requestParams.put("user_id", mPreferenceManager.getUserID());

                Requests.post("/user/" + mPreferenceManager.getUserID() + "/change-photo", requestParams, textHttpResponseHandler);
            }catch (Exception e) {}
        }

        @Override
        public void onError(Throwable throwable) {

        }
    };
}

