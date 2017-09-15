package com.beem24.projects.devafrica.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.ImageCompressionTask;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Photo;
import com.beem24.projects.devafrica.entities.Post;
import com.beem24.projects.devafrica.entities.Tag;
import com.beem24.projects.devafrica.interfaces.IImageCompressionTaskListener;
import com.beem24.projects.devafrica.ui.adapters.PhotoModelAdapter;
import com.beem24.projects.devafrica.ui.adapters.TagListAdapter;
import com.beem24.projects.devafrica.ui.views.EditTextDialog;
import com.beem24.projects.devafrica.util.BitmapUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import io.github.mthli.knife.KnifeText;

/**
 * Created By Adigun Hammed Olalekan
 * 7/5/2017.
 * Beem24, Inc
 */

public class NewTopicActivity extends BaseActivity {

    @BindView(R.id.title_edt_new_topic)
    EditText mTitle;
    @BindView(R.id.knife_edt)
    KnifeText mKnifeText;
    @BindView(R.id.tag_rv_new_topic)
    RecyclerView mTagsRecyclerView;
    @BindView(R.id.rv_image_selected_new_topic)
    RecyclerView mPhotoRecyclerView;

    public static final int PICK_IMAGE_REQUEST = 1001;
    public static final int REQUEST_STORAGE_PERMISSION = 1002;

    private String mPath = "";
    private List<String> mImages = new ArrayList<>();
    private EditTextDialog editTextDialog;
    private List<Photo> mPhotoList = new ArrayList<>();
    private PhotoModelAdapter photoModelAdapter;
    private List<Tag> mTags = new ArrayList<>();
    private TagListAdapter tagListAdapter;

    private EditTextDialog.IEditTextDialogListener iEditTextDialogListener = new EditTextDialog.IEditTextDialogListener() {
        @Override
        public void onFinish(String text, int ID) {
            switch (ID) {
                case 101:
                    int start = mKnifeText.getSelectionStart();
                    int end = mKnifeText.getSelectionEnd();
                    mKnifeText.link(text, start, end);
                    break;
                case 100:
                    processTags(text.trim());
                    break;
            }
        }

        @Override
        public void onCancel() {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_create_post);

        mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_STORAGE_PERMISSION && grantResults.length > 0) {
            if(PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent i = Intent.createChooser(intent, "Choose Image");
                startActivityForResult(i, PICK_IMAGE_REQUEST);
            }else {
                showDialog("Permission Error", "Storage permission denied.");
            }
        }
    }

    void requestPermission() {
        boolean granted = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(granted) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            Intent i = Intent.createChooser(intent, "Choose Image");
            startActivityForResult(i, PICK_IMAGE_REQUEST);
        }else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_topic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_item_publish_post:
                try {
                    String title = mTitle.getText().toString().trim();
                    String content = mKnifeText.toHtml();
                    Log.d(DevAfrica.TAG, content);

                    if(TextUtils.isEmpty(title)) {
                        showDialog("Error", "Write topic title");
                        return true;
                    }
                    if(TextUtils.isEmpty(content)) {
                        showDialog("Error", "Write post content");
                        return true;
                    }
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("user_id", PreferenceManager.getInstance().getUserID());
                    requestParams.put("post_title", title);
                    requestParams.put("post_content", content);
                    for (int i = 0; i < mPhotoList.size(); i++) {
                        String paramName = "photo_" + i;
                        requestParams.put(paramName, new File(mPhotoList.get(i).mPath));
                    }
                    for (int i = 0; i < mTags.size(); i++) {
                        String paramName = "tag_" + i;
                        requestParams.put(paramName, mTags.get(i).tagName);
                    }
                    Log.d(DevAfrica.TAG, requestParams.toString());
                    Requests.post("/post", requestParams, textHttpResponseHandler);

                }catch (Exception e) {
                    Log.d(DevAfrica.TAG, "ERROR", e);
                }

        }
        return super.onOptionsItemSelected(item);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            showDialog("Error", "Failed to create topic. Please retry.");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                JSONObject postJsonObject = jsonObject.getJSONObject("data");
                Post post = Post.from(postJsonObject);
                Log.d(DevAfrica.TAG, post.rawPost());
                toast("Success");
            }catch (JSONException je) {
                Log.d(DevAfrica.TAG, "ERROR", je);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            findViewById(R.id.loading_layout_new_topic).setVisibility(View.GONE);
        }

        @Override
        public void onStart() {
            super.onStart();
            findViewById(R.id.loading_layout_new_topic).setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;

        Uri uri = data.getData();
        switch (requestCode) {
            case PICK_IMAGE_REQUEST:
                String path = BitmapUtils.getPath(this, uri);
                upload(path);
                break;
        }
    }
    void upload(String path) {
        List<String> strings = new ArrayList<>();
        strings.clear();
        strings.add(path);
        DevAfrica.mExecutorService.execute(new ImageCompressionTask(this, iImageCompressionTaskListener
                , strings, PICK_IMAGE_REQUEST));
    }

    private IImageCompressionTaskListener iImageCompressionTaskListener = new IImageCompressionTaskListener() {
        @Override
        public void onCompressed(List<File> file, int id) {
            if(file.size() <= 0) {

                return;
            }
            File f = file.get(0);
            mPhotoList.add(new Photo(f.getAbsolutePath()));
            if(photoModelAdapter == null) {
                photoModelAdapter = new PhotoModelAdapter(NewTopicActivity.this, mPhotoList);
                mPhotoRecyclerView.setAdapter(photoModelAdapter);
            }
            photoModelAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(Throwable throwable) {
            Log.d(DevAfrica.TAG, "ERROR", throwable);
        }
    };
    @OnClick(R.id.btn_bold) public void onBoldClick() {
        mKnifeText.bold(!mKnifeText.contains(KnifeText.FORMAT_BOLD));
    }
    @OnClick(R.id.btn_italic) public void onItalicClick() {
        mKnifeText.italic(!mKnifeText.contains(KnifeText.FORMAT_ITALIC));
    }
    @OnClick(R.id.btn_bulleted_list) public void onBulletClick() {
        mKnifeText.bullet(!mKnifeText.contains(KnifeText.FORMAT_BULLET));
    }
    @OnClick(R.id.btn_insert_image) public void onInsertImage() {
        requestPermission();
    }
    @OnClick(R.id.btn_insert_link) public void onInsertLinkClick() {
        EditTextDialog editTextDialog = new EditTextDialog(this, "Insert Link", "Insert Link", iEditTextDialogListener, 101);
        editTextDialog.show();
    }
    @OnClick(R.id.btn_insert_tag) public void onInsertTag() {
        EditTextDialog editTextDialog = new EditTextDialog(this, "Insert Tag", "Insert comma separated list of tags", iEditTextDialogListener,
                100);
        editTextDialog.show();
    }
    void processTags(String tags) {
        if(tags.length() <= 0)
            return;

        String[] splited = tags.split(",");
        List<Tag> result = new ArrayList<>();
        for (int i = 0; i < splited.length; i++) {
            result.add(new Tag(splited[i].trim()));
        }
        if(result.size() > 0) {
            mTags.clear();
           for (Tag tag : result)
               mTags.add(tag);
        }
        result.clear();
        if(tagListAdapter == null) {
            tagListAdapter = new TagListAdapter(mTags, this);
            mTagsRecyclerView.setAdapter(tagListAdapter);
        }
        tagListAdapter.notifyDataSetChanged();
    }
}
