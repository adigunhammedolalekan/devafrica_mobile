package com.beem24.projects.devafrica.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Comment;
import com.beem24.projects.devafrica.ui.adapters.CommentAdapter;
import com.beem24.projects.devafrica.ui.adapters.PostReplyAdapter;
import com.beem24.projects.devafrica.util.Util;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

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

public class PostReplyActivity extends BaseActivity implements CommentAdapter.ICommentMenuItemClickListener{

    @BindView(R.id.pw_post_replies)
    ProgressWheel progressWheel;
    @BindView(R.id.post_replies_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.send_reply_btn)
    MaterialIconView sendReply;
    @BindView(R.id.edt_reply)
    EditText replyEditText;
    @BindView(R.id.iv_reply)
    CircleImageView replyDP;
    @BindView(R.id.tv_post_op_post_replies)
    TextView opNameTextView;
    @BindView(R.id.tv_reply_content_post_replies)
    HtmlTextView mHtmlTextView;

    private Comment currentComment;
    private List<Comment> replies = new ArrayList<>();
    private PostReplyAdapter commentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_post_replies);
        Intent intent = getIntent();
        if(intent == null) {
            finish();return;
        }
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Replies");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setNestedScrollingEnabled(false);
        String JSON = intent.getStringExtra("raw_comment");
        replyEditText.addTextChangedListener(textWatcher);
        currentComment = parse(JSON);
        mHtmlTextView.setHtml(currentComment.commentBody);
        opNameTextView.setText(currentComment.mUser.username);
        getReplies();
    }
    void getReplies() {
        if(!Util.isOnline(this)) {
            Snackbar.make(replyEditText, "Device is offline", Snackbar.LENGTH_INDEFINITE).show();
            return;
        }
        Requests.get("/comment/" + currentComment.mCommentID + "/replies", textHttpResponseHandler);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.d(DevAfrica.TAG, "ERROR"+responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Log.d(DevAfrica.TAG, responseString);
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Comment comment = Comment.fromReply(jsonArray.getJSONObject(i));
                    replies.add(comment);
                }
                commentAdapter = new PostReplyAdapter(PostReplyActivity.this, replies);
                mRecyclerView.setAdapter(commentAdapter);
            }catch (JSONException je) {
                Log.d(DevAfrica.TAG, "ERROR", je);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            progressWheel.setVisibility(View.GONE);
        }

    };
    Comment parse(String JSON) {
        try {
            return Comment.from(new JSONObject(JSON));
        }catch (JSONException e) {}
        return new Comment();
    }
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.toString().length() <= 0)
                sendReply.setEnabled(false);
            else
                sendReply.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    @OnClick(R.id.send_reply_btn) public void onSendReplyClick() {
        String content = replyEditText.getText().toString().trim();
        if(TextUtils.isEmpty(content)) {
            toast("Cannot post an empty reply.");
            return;
        }

        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", PreferenceManager.getInstance().getUserID());
        requestParams.put("content", content);
        requestParams.put("op_id", currentComment.mUser.ID);

        Util.hideKeyboard(this);
        Requests.post("/comment/" + currentComment.mCommentID + "/reply", requestParams, responseHandler);
    }
    TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("Error occurred. Failed to post reply. Please retry.");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                Comment comment = Comment.fromReply(jsonObject.getJSONObject("data"));
                replies.add(comment);
                toast("Reply Posted!");
                replyEditText.setText("");
            }catch (JSONException je) {}
            commentAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(replies.size() - 1);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            findViewById(R.id.pw_send_reply).setVisibility(View.GONE);
            findViewById(R.id.send_reply_btn).setVisibility(View.VISIBLE);
        }

        @Override
        public void onStart() {
            super.onStart();
            findViewById(R.id.pw_send_reply).setVisibility(View.VISIBLE);
            findViewById(R.id.send_reply_btn).setVisibility(View.GONE);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReportClick(int position) {

    }

    @Override
    public void onMentionClick(int position) {

    }

    @Override
    public void onViewProfileClick(int position) {

    }
}
