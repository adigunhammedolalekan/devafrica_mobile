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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.ImageCompressionTask;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.interfaces.IImageCompressionTaskListener;
import com.beem24.projects.devafrica.util.BitmapUtils;
import com.beem24.projects.devafrica.util.Util;
import com.bumptech.glide.Glide;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/8/2017.
 * Beem24, Inc
 */

public class UpdateAccountActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    private static final int REQUEST_SELECT_STACK = 1559;

    @BindView(R.id.iv_user_set_up_your_account)
    CircleImageView circleImageView;
    @BindView(R.id.phone_number_edt_set_up_account)
    MaterialEditText phoneNumberEditText;
    @BindView(R.id.spinner_select_country)
    Spinner mSpinner;
    @BindView(R.id.layout_select_language)
    RelativeLayout selectLangLayout;
    @BindView(R.id.stack_tv_set_up_account)
    TextView stackListTextView;
    @BindView(R.id.bio_edt_set_up_account)
    MaterialEditText bioEditText;

    private boolean isFBSignUp = false;

    String selectedLanguages = "";
    String countrySelected = "";
    String[] countries;

    String mPhoto = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_up_account);
        countries = getResources().getStringArray(R.array.countries);
        mSpinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.countries, android.R.layout.simple_spinner_dropdown_item));
        mSpinner.setOnItemSelectedListener(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Update Account");
        }
        Intent intent = getIntent();

    }
    @OnClick(R.id.iv_user_set_up_your_account) public void onDPClick() {
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
            if(requestCode == REQUEST_SELECT_STACK) {
                selectedLanguages = data.getStringExtra("_selected_");
                if(TextUtils.isEmpty(selectedLanguages)) {
                    stackListTextView.setText("No language selected.");
                }else {
                    stackListTextView.setText(selectedLanguages);
                }
            }else {
                Uri uri = data.getData();
                String path = BitmapUtils.getPath(this, uri);
                Glide.with(this)
                        .load(new File(path)).error(R.color.divider)
                        .placeholder(R.color.divider).dontAnimate().into(circleImageView);
                List<String> s = new ArrayList<>();
                s.add(path);
                DevAfrica.mExecutorService.execute(new ImageCompressionTask(this,iImageCompressionTaskListener , s, 1 ));
            }
        }
    }

    private IImageCompressionTaskListener iImageCompressionTaskListener = new IImageCompressionTaskListener() {
        @Override
        public void onCompressed(List<File> file, int id) {
            if(file == null)
                return;

            mPhoto = file.get(0).getAbsolutePath();
        }

        @Override
        public void onError(Throwable throwable) {

        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_follow_user, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_proceed:
                if(Util.empty(phoneNumberEditText)) {
                    toast("Enter your phone number");
                    return true;
                }
                if(TextUtils.isEmpty(countrySelected)) {
                    toast("Select your country.");
                    return true;
                }

                RequestParams requestParams = new RequestParams();
                requestParams.put("phone_number", Util.text(phoneNumberEditText));
                requestParams.put("country", countrySelected);
                requestParams.put("langs", selectedLanguages);
                requestParams.put("bio", Util.text(bioEditText));
                requestParams.put("user_id", PreferenceManager.getInstance().getUserID());

                try {
                    requestParams.put("dp_", new File(mPhoto));
                }catch (Exception e) {}
                Requests.post("/user/update", requestParams, textHttpResponseHandler);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("There was an error. Please retry.");
            Log.d(DevAfrica.TAG, "ERROR" + responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Log.d(DevAfrica.TAG, responseString);
            Intent intent = new Intent(UpdateAccountActivity.this, FollowUserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            findViewById(R.id.loading_layout_set_up_account).setVisibility(View.GONE);
        }

        @Override
        public void onStart() {
            super.onStart();
            findViewById(R.id.loading_layout_set_up_account).setVisibility(View.VISIBLE);
        }
    };
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        countrySelected = countries[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        countrySelected = "";
    }

    @OnClick(R.id.layout_select_language) public void onSelectLangClick() {
        Intent intent = new Intent(this, ActivitySelectStack.class);
        startActivityForResult(intent, REQUEST_SELECT_STACK);
    }
}
