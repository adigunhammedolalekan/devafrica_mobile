package com.beem24.projects.devafrica.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.ImageCompressionTask;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Follower;
import com.beem24.projects.devafrica.entities.Tag;
import com.beem24.projects.devafrica.interfaces.IImageCompressionTaskListener;
import com.beem24.projects.devafrica.local.DatabaseManager;
import com.beem24.projects.devafrica.ui.adapters.MentionListAdapter;
import com.beem24.projects.devafrica.ui.adapters.TagListAdapter;
import com.beem24.projects.devafrica.ui.views.EditTextDialog;
import com.beem24.projects.devafrica.util.BitmapUtils;
import com.github.irshulx.Editor;
import com.github.irshulx.EditorListener;
import com.github.irshulx.models.EditorTextStyle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created By Adigun Hammed Olalekan
 * 7/15/2017.
 * Beem24, Inc
 */

public class ActivityNewTopic extends BaseActivity implements MentionListAdapter.IMentionClickListener{

    @BindView(R.id.editor_new_topic)
     Editor mEditor;
    @BindView(R.id.rv_topic_tags)
    RecyclerView mTagsRecyclerView;
    @BindView(R.id.edt_topic_title)
    EditText mTitleEditText;
    @BindView(R.id.rv_suggestion_list)
    RecyclerView mSuggestionListRecyclerView;
    @BindView(R.id.suggestion_list_container_new_topic)
    RelativeLayout mSuggestionContainer;


    private EditTextDialog editTextDialog;

    public static final int REQUEST_STORAGE_PERMISSION_CODE = 1009;


    private Map<String, String> mPendingUploads = new HashMap<>();
    private List<String> mImages = new ArrayList<>();
    private List<Tag> mTags = new ArrayList<>();
    private List<Follower> mFollowerList;

    private TagListAdapter tagListAdapter;
    private MentionListAdapter mentionListAdapter;


    private String mUIID = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_new_post);
        mTagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSuggestionListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mentionListAdapter = new MentionListAdapter(this, this);
        mSuggestionListRecyclerView.setAdapter(mentionListAdapter);
        mEditor.render();

        mEditor.setEditorListener(editorListener);
        mEditor.setHeadingTypeface(getHeaderTypeFace());
        mEditor.setContentTypeface(getHeaderTypeFace());

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("New Topic");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mFollowerList = DatabaseManager.getInstance().getFollowers();

    }
    @OnClick(R.id.btn_h1) public void onH1Click() {
        mEditor.updateTextStyle(EditorTextStyle.H1);
    }
    @OnClick(R.id.btn_h2) public void onH2Click() {
        mEditor.updateTextStyle(EditorTextStyle.H2);
    }
    @OnClick(R.id.btn_h3) public void onH3Click() {
        mEditor.updateTextStyle(EditorTextStyle.H3);
    }
    @OnClick(R.id.btn_bold) public void onBold() {
        mEditor.updateTextStyle(EditorTextStyle.BOLD);
    }
    @OnClick(R.id.btn_italic) public void onItalic() {
        mEditor.updateTextStyle(EditorTextStyle.ITALIC);
    }
    @OnClick(R.id.btn_mumbered_list) public void onInsertNumList() {
        mEditor.insertList(true);
    }
    @OnClick(R.id.btn_bulleted_list) public void onInsertBulletList() {
        mEditor.insertList(false);
    }
    @OnClick(R.id.btn_hr) public void onHRClick() {
        mEditor.insertDivider();
    }
    @OnClick(R.id.btn_insert_link) public void onInsertLink() {
        editTextDialog = new EditTextDialog(this, "Insert Link", "https://www.google.com", new EditTextDialog.IEditTextDialogListener() {
            @Override
            public void onFinish(String text, int id) {
                mEditor.insertLink(text);
            }

            @Override
            public void onCancel() {

            }
        }, 100);
        editTextDialog.show();
    }
    @OnClick(R.id.btn_insert_image) public void onInsertImage() {
        requestPermission();
    }
    @OnClick(R.id.btn_insert_tag) public void onInsertTag() {
        editTextDialog = new EditTextDialog(this, "Insert Tag", "add comma separated list of tags", new EditTextDialog.IEditTextDialogListener() {
            @Override
            public void onFinish(String text, int id) {
                processTags(text);
            }

            @Override
            public void onCancel() {

            }
        }, 1033);
        editTextDialog.show();
    }
    void requestPermission() {
        boolean granted = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(granted) {
            mEditor.openImagePicker();
        }else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION_CODE);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mEditor.openImagePicker();
            }else {
                showDialog("Permission Denied", "Failed to select photo. Permission denied.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == mEditor.PICK_IMAGE_REQUEST) {
            Uri uri = data.getData();
            String path = BitmapUtils.getPath(this, uri);
            List<String> strings = new ArrayList<>();
            strings.add(path);
            DevAfrica.mExecutorService.execute(new ImageCompressionTask(this, iImageCompressionTaskListener , strings, 1010));

        }
    }
    private IImageCompressionTaskListener iImageCompressionTaskListener = new IImageCompressionTaskListener() {
        @Override
        public void onCompressed(List<File> file, int id) {
            if(file == null || file.size() <= 0)
                return;

            String path = file.get(0).getAbsolutePath();
           //

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            mEditor.insertImage(bitmap);
            RequestParams requestParams = new RequestParams();
            mPendingUploads.put(mUIID, path);
            try{
                requestParams.put("img_", new File(path));
            }catch (Exception e) {
                Log.d(DevAfrica.TAG, "ERROR", e);

            }
            Log.d(DevAfrica.TAG, requestParams.toString());
            Requests.post("/post/photo", requestParams, textHttpResponseHandler);
        }

        @Override
        public void onError(Throwable throwable) {

        }
    };
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            mEditor.onImageUploadFailed(mUIID);
            Log.d(DevAfrica.TAG, "ERROR" + responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Log.d(DevAfrica.TAG, responseString + "_");
            if(responseString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    String remotePath = jsonObject.getString("uri");
                    String id = jsonObject.getString("id");
                    mImages.add(id);
                    mEditor.onImageUploadComplete(remotePath, mUIID);
                }catch (JSONException je) {}
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    };
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
                String JSON = mEditor.getContentAsSerialized();
                if(TextUtils.isEmpty(mTitleEditText.getText().toString())) {
                    showDialog("Error", "Write topic title");
                    return true;
                }
                if(TextUtils.isEmpty(JSON)) {
                    showDialog("Error", "Post is empty.");
                    return true;
                }
                RequestParams requestParams = new RequestParams();
                requestParams.put("user_id", PreferenceManager.getInstance().getUserID());
                requestParams.put("post_title", mTitleEditText.getText().toString().trim());
                requestParams.put("post_content", JSON);
                requestParams.put("photo_count", mImages.size());
                for (int i = 0; i < mImages.size(); i++) {
                    String paramName = "_id" + i;
                    requestParams.put(paramName, mImages.get(i));
                }
                requestParams.put("tag_count", mTags.size());
                for (int i = 0; i < mTags.size(); i++) {
                    String paramName = "_tag" + i;
                    requestParams.put(paramName, mTags.get(i).tagName.trim());
                }

                Requests.post("/post", requestParams, postHttpResponseHandler);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private TextHttpResponseHandler postHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("There seem to be an error. Please retry.");
            Log.d(DevAfrica.TAG, "ERROR" + responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            //Log.d(DevAfrica.TAG, responseString + "_");

            Intent intent = new Intent(ActivityNewTopic.this, CongratulationActivity.class);
            intent.putExtra("_data_", responseString);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onStart() {
            super.onStart();
        }
    };
    private EditorListener editorListener = new EditorListener() {
        @Override
        public void onTextChanged(EditText editText, Editable text) {

        }

        @Override
        public void onUpload(Bitmap image, String uuid) {
            mUIID = uuid;
        }
    };
    void processTags(String tags) {
        String[] splitted = tags.split(",");
        if(splitted.length <= 0)
            return;

        for (int i = 0; i < splitted.length; i++) {
            String tag = splitted[i].trim();
            Log.d(DevAfrica.TAG, tag + "_");
            mTags.add(new Tag(tag));
        }
        findViewById(R.id.tag_details_container_new_topic).setVisibility(View.VISIBLE);
        tagListAdapter = new TagListAdapter(mTags, this);
        mTagsRecyclerView.setAdapter(tagListAdapter);
    }
    private Map<Integer, String> getHeaderTypeFace() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/Lato-Medium.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/Lato-Black.ttf");
        return typefaceMap;
    }

    @Override
    public void onMentionClick(String name) {

    }
    void processMention(String mention) {

        String[] mentions = mention.split(" ");
        for (String next : mentions) {
            next = next.trim();
            if(next.charAt(0) == '@') {
                mSuggestionContainer.setVisibility(View.VISIBLE);

            }else {
                mSuggestionContainer.setVisibility(View.GONE);
            }
            mentionListAdapter.add(new Follower(next));
        }
    }
}
