package com.beem24.projects.devafrica.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.FcmNotificationSender;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.BookMark;
import com.beem24.projects.devafrica.entities.Comment;
import com.beem24.projects.devafrica.entities.Follower;
import com.beem24.projects.devafrica.entities.Post;
import com.beem24.projects.devafrica.entities.Queue;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.local.DatabaseManager;
import com.beem24.projects.devafrica.ui.adapters.CommentAdapter;
import com.beem24.projects.devafrica.ui.views.EndLessScrollListener;
import com.beem24.projects.devafrica.util.Util;
import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.irshulx.Editor;
import com.github.irshulx.models.EditorContent;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/5/2017.
 * Beem24, Inc
 *
 */

public class TopicDetailsActivity extends BaseActivity implements CommentAdapter.ICommentMenuItemClickListener{

    String JSON = "";

    Post currentPost;

    @BindView(R.id.topic_title_tv_topic_details)
    TextView mTitle;
    @BindView(R.id.topic_content_tv)
    Editor mHtmlTextView;
    @BindView(R.id.op_name_tv_post_details)
    TextView username;
    @BindView(R.id.iv_op_post_details)
    CircleImageView dp;
    @BindView(R.id.tv_time_posted_post_details)
    TextView timePosted;
    @BindView(R.id.rv_post_response_list)
    RecyclerView mPostResponsesRecyclerView;
    @BindView(R.id.pw_topic_details)
    ProgressWheel progressWheel;
    @BindView(R.id.tv_response_count_topic_details)
    TextView mResponseCount;
    @BindView(R.id.btn_follow_topic_details)
    Button mFollowButton;
    @BindView(R.id.rv_tag_list_post_details)
    RecyclerView mTagListRecyclerView;
    @BindView(R.id.post_tag_container_layout_post_details)
    LinearLayout mTagContainer;
    @BindView(R.id.iv_write_response)
    CircleImageView ivWriteResponse;
    @BindView(R.id.pw_load_more_response)
    ProgressWheel mProgressWheel;

    private List<Comment> postResponses = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private MenuItem mBookmarkedMenuItem;
    private MenuItem notBookmarkedMenuItem;
    private MenuItem shareMenuItem;

    private DatabaseManager mDatabaseManager;

    private volatile boolean mLoading = false;

    private Follower follower = new Follower();

    User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_details_activity);
        Intent intent = getIntent();
        if(intent == null) {finish();return;}
        JSON = intent.getStringExtra("raw_post");
        currentPost = parse(JSON);
        follower.ID = Integer.parseInt(currentPost.user.ID);
        follower.mUsername = currentPost.user.username;
        mHtmlTextView.setContentTypeface(getHeaderTypeFace());
        mHtmlTextView.setHeadingTypeface(getHeaderTypeFace());

        currentUser = PreferenceManager.getInstance().getUser();
        mDatabaseManager = DatabaseManager.getInstance();

        render(currentPost);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mPostResponsesRecyclerView.setLayoutManager(mLinearLayoutManager);
        mPostResponsesRecyclerView.setNestedScrollingEnabled(false);
        mPostResponsesRecyclerView.addOnScrollListener(new EndLessScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                paginate(totalItemsCount);
            }
        });

        mResponseCount.setText(currentPost.commentCount > 1 ?
                currentPost.commentCount + " Responses." : currentPost.commentCount + " Response.");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        getPostComments();
    }
    Post parse(String json) {
        try {
            return Post.from(new JSONObject(json));
        }catch (JSONException je) {
            Log.d(DevAfrica.TAG, "ERROR", je);
        }
        return new Post();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_item_bookmark_false:
                currentPost.bookmarked = true;
                BookMark bookMark = new BookMark();
                bookMark.mPostID = currentPost.postID;
                bookMark.JSON = currentPost.rawPost();
                mDatabaseManager.bookmark(bookMark);

                setMenuItemState();
                break;
            case R.id.menu_item_bookmark_true:
                currentPost.bookmarked = false;
                BookMark book = new BookMark();
                book.mPostID = currentPost.postID;
                book.JSON = currentPost.rawPost();
                mDatabaseManager.deleteBookmark(book);

                setMenuItemState();
                break;
            case R.id.menu_item_share_post:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    void render(Post post) {
        EditorContent editorContent = mHtmlTextView.getContentDeserialized(post.postContent);
        mHtmlTextView.render(editorContent);
        mTitle.setText(post.postTitle);
        mTitle.setTextSize(28);
        username.setText(post.user.username);
        timePosted.setText(post.timeCreated);
        if(!currentUser.mPhoto.isEmpty()) {
            Glide.with(this).load(currentUser.mPhoto)
                    .error(R.color.divider).placeholder(R.color.divider)
                    .dontAnimate().into(ivWriteResponse);
        }

        if(!post.user.mPhoto.isEmpty()) {
            Glide.with(this)
                    .load(post.user.mPhoto)
                    .placeholder(R.color.divider).error(R.color.divider)
                    .dontAnimate().into(dp);
        }

        if(DatabaseManager.getInstance().isFollowing(follower)) {
            mFollowButton.setText("FOLLOWING");
            mFollowButton.setBackgroundResource(R.drawable.btn_bg_green);
            mFollowButton.setTextColor(ContextCompat.getColor(this, R.color.green));
        }else {
            mFollowButton.setText("FOLLOW");
            mFollowButton.setBackgroundResource(R.drawable.btn_bg);
            mFollowButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        if(TextUtils.equals(currentUser.ID, currentPost.user.ID)) {
            mFollowButton.setVisibility(View.GONE);
        }
    }
    void getPostComments() {
        if (!Util.isOnline(this)) {
            Snackbar.make(mHtmlTextView, "Device is offline", Snackbar.LENGTH_INDEFINITE);
            return;
        }

        Requests.get("/post/" + currentPost.postID + "/comments", textHttpResponseHandler);
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
                JSONArray jsonArray = new JSONArray(responseString);
                if(jsonArray.length() <= 0) {
                    findViewById(R.id.layout_no_post_response).setVisibility(View.VISIBLE);
                }else {
                    postResponses.clear();
                    findViewById(R.id.layout_no_post_response).setVisibility(View.INVISIBLE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Comment comment = Comment.from(jsonArray.getJSONObject(i));
                        postResponses.add(comment);
                    }
                    commentAdapter = new CommentAdapter(TopicDetailsActivity.this, postResponses, false);
                    commentAdapter.addListener(TopicDetailsActivity.this);
                    mPostResponsesRecyclerView.setAdapter(commentAdapter);
                }

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
    @OnClick(R.id.layout_write_response) public void onWriteResponseClick() {
        Intent intent = new Intent(this, ActivityWriteResponse.class);
        intent.putExtra("post_id", currentPost.postID);
        intent.putExtra("op_id", currentPost.user.ID);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(resultCode == RESULT_OK) {
                String response = data.getStringExtra("new_comment");
                JSONObject jsonObject = new JSONObject(response);
                Comment comment = Comment.from(jsonObject.getJSONObject("comment"));
                postResponses.add(comment);
                if(commentAdapter == null) {
                    commentAdapter = new CommentAdapter(this, postResponses, false);
                    mPostResponsesRecyclerView.setAdapter(commentAdapter);
                }else {
                    commentAdapter.notifyDataSetChanged();
                }
                mPostResponsesRecyclerView.scrollToPosition(postResponses.size() - 1);
                FcmNotificationSender notificationSender = new FcmNotificationSender.Builder()
                        .message(comment.commentBody).photo(currentUser.mPhoto)
                        .to("/topics/" + currentPost.postID).userID(currentUser.ID)
                        .username(currentUser.username).build();
                notificationSender.send();
                toast("Response Posted!");
            }
        }catch (JSONException je) {}
    }
    @OnClick(R.id.btn_follow_topic_details) public void onFollowClick() {
        final Queue queue = new Queue();
        if(DatabaseManager.getInstance().isFollowing(follower)) {
            new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                    .setTitle("UnFollow " + currentPost.user.username)
                    .setMessage("Do you really want to unFollow " + currentPost.user.username)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            currentPost.user.selected = false;
                            queue.mURL = Requests.BASE_URL + "/user/unfollow";
                            queue.mRequestParams.put("user_id", PreferenceManager.getInstance().getUserID());
                            queue.mRequestParams.put("master_id", currentPost.user.ID);
                            mFollowButton.setBackgroundResource(R.drawable.btn_bg);
                            mFollowButton.setTextColor(ContextCompat.getColor(TopicDetailsActivity.this, R.color.white));
                            mFollowButton.setText("FOLLOW");
                            DatabaseManager.getInstance().removeFollower(follower);
                        }
                    }).setNegativeButton("CANCEL", null).create().show();
        }else {
            currentPost.user.selected = true;
            queue.mURL = Requests.BASE_URL + "/user/follow";
            queue.mRequestParams.put("user_id", PreferenceManager.getInstance().getUserID());
            queue.mRequestParams.put("master_id", currentPost.user.ID);
            mFollowButton.setBackgroundResource(R.drawable.btn_bg_green);
            mFollowButton.setTextColor(ContextCompat.getColor(this, R.color.green));
            mFollowButton.setText("FOLLOWING");

            DatabaseManager.getInstance().addFollower(follower);
        }
        DevAfrica.getApp().addJob(queue);
    }

    @Override
    public void onReportClick(int position) {
        toast("Coming Soon");
    }

    @Override
    public void onMentionClick(int position) {
        Comment comment = postResponses.get(position);
        Intent intent = new Intent(this, ActivityWriteResponse.class);
        intent.putExtra("to_mention", comment.mUser.raw());
        startActivity(intent);
    }

    @Override
    public void onViewProfileClick(int position) {
        Comment comment = postResponses.get(position);
        Intent intent = new Intent(this, ActivityProfile.class);
        intent.putExtra("raw_user", comment.mUser.raw());
        startActivity(intent);
    }
    private Map<Integer, String> getHeaderTypeFace() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/Lato-Medium.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/Lato-Black.ttf");
        return typefaceMap;
    }
    void paginate(int count) {
        if(!Util.isOnline(this)) {
            return;
        }
        if(mLoading)
            return;

        mLoading = true;
        Requests.get("/posts/" + currentPost.postID + "/comments/paginate/" + count, paginateResponseHandler);
    }
    private TextHttpResponseHandler paginateResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Comment comment = Comment.from(object);
                    postResponses.add(comment);
                }
                int count = commentAdapter.getItemCount();
                commentAdapter.notifyItemRangeChanged(count, (postResponses.size() - 1));
            }catch (JSONException je) {}
        }

        @Override
        public void onStart() {
            super.onStart();
            mProgressWheel.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mLoading = false;
            mProgressWheel.setVisibility(View.GONE);
        }
    };
    @OnClick(R.id.op_layout_topic_details) public void onOpLayoutClick() {
        Intent intent = new Intent(this, ActivityProfile.class);
        intent.putExtra("raw_user", currentPost.user.raw());
        startActivity(intent);
    }
    @OnClick(R.id.iv_op_post_details) public void onOpIvClick() {
        Intent intent = new Intent(this, ActivityProfile.class);
        intent.putExtra("raw_user", currentPost.user.raw());
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_topic_details, menu);

        mBookmarkedMenuItem = menu.findItem(R.id.menu_item_bookmark_true);
        notBookmarkedMenuItem = menu.findItem(R.id.menu_item_bookmark_false);
        shareMenuItem = menu.findItem(R.id.menu_item_share_post);

        setMenuItemState();
        return super.onCreateOptionsMenu(menu);
    }
    void setMenuItemState() {
        if(currentPost.bookmarked) {
            mBookmarkedMenuItem.setVisible(true);
            notBookmarkedMenuItem.setVisible(false);
        }else {
            notBookmarkedMenuItem.setVisible(true);
            mBookmarkedMenuItem.setVisible(false);
        }
    }
}
