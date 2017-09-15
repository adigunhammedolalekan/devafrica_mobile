package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.BookMark;
import com.beem24.projects.devafrica.entities.Post;
import com.beem24.projects.devafrica.local.DatabaseManager;
import com.beem24.projects.devafrica.ui.activities.TopicDetailsActivity;
import com.bumptech.glide.Glide;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/5/2017.
 * Beem24, Inc
 */

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    public static final int ROW_TYPE_TOPIC = 1;
    public static final int ROW_TYPE_IMAGE = 2;

    private LayoutInflater mLayoutInflater;
    private List<Post> mPostList;
    private Context mContext;
    private DatabaseManager mDatabaseManager;

    public TopicAdapter(Context context) {
        mContext = context;
        mPostList = new ArrayList<>();
        if(mContext != null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        mDatabaseManager = DatabaseManager.getInstance();
    }
    public TopicAdapter(Context context, List<Post> posts) {
        this(context);
        mPostList = posts;
    }
    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
        if(viewType == ROW_TYPE_TOPIC) {
            return new OrdinaryTopicViewHolder(mLayoutInflater.inflate(R.layout.layout_post_item, parent, false));
        }else if(viewType == ROW_TYPE_IMAGE) {
            return new TopicWithPhotoViewHolder(mLayoutInflater.inflate(R.layout.layout_post_item_with_image, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, final int position) {
        final Post post = mPostList.get(position);

        if(holder instanceof OrdinaryTopicViewHolder) {
            final OrdinaryTopicViewHolder ordinaryTopicViewHolder = (OrdinaryTopicViewHolder)
                    holder;
            ordinaryTopicViewHolder.opName.setText(post.user.username);
            ordinaryTopicViewHolder.postTitle.setText(post.postTitle);
            String content = post.getPostDescription();

            if(!post.user.mPhoto.isEmpty()) {
                Glide.with(mContext).load(post.user.mPhoto)
                        .placeholder(R.color.divider)
                        .error(R.color.divider).dontAnimate().into(ordinaryTopicViewHolder.circleImageView);
            }else {
                ordinaryTopicViewHolder.circleImageView.setImageResource(R.color.divider);
            }

            ordinaryTopicViewHolder.mHtmlTextView.setHtml(content);
            ordinaryTopicViewHolder.timePosted.setText(post.timeCreated);
            ordinaryTopicViewHolder.commentCount.setText(
                    post.commentCount > 1 ? post.commentCount + " Responses" : post.commentCount + " Response"
            );
            ordinaryTopicViewHolder.comment.setColorFilter(ContextCompat.getColor(mContext, R.color.textColorSecondary));
            if(mDatabaseManager.postBookmarked(post)) {
                ordinaryTopicViewHolder.addToBookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
            }else {
                ordinaryTopicViewHolder.addToBookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
            }
            ordinaryTopicViewHolder.addToBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(post.bookmarked) {
                        BookMark bookMark = new BookMark();
                        bookMark.mPostID = post.postID;

                        mDatabaseManager.deleteBookmark(bookMark);
                        post.bookmarked = false;
                    }else {
                        BookMark bookMark = new BookMark();
                        bookMark.mPostID = post.postID;
                        bookMark.JSON = post.rawPost();

                        mDatabaseManager.bookmark(bookMark);
                        post.bookmarked = true;
                    }
                    notifyItemChanged(position);
                }
            });
        }else {
            TopicWithPhotoViewHolder topicWithPhotoViewHolder = (TopicWithPhotoViewHolder) holder;
            String URL = post.getThumbURI();
            Glide.with(mContext)
                    .load(URL).error(R.color.divider)
                    .placeholder(R.color.divider).centerCrop().into(topicWithPhotoViewHolder.mImageView);
            if(!post.user.mPhoto.isEmpty()) {
                Glide.with(mContext).load(post.user.mPhoto)
                        .placeholder(R.color.divider)
                        .error(R.color.divider).dontAnimate().into(topicWithPhotoViewHolder.circleImageView);

            }else {
                topicWithPhotoViewHolder.circleImageView.setImageResource(R.color.divider);
            }
            topicWithPhotoViewHolder.titleTextView.setText(post.postTitle);
            topicWithPhotoViewHolder.opName.setText(post.user.username);
            topicWithPhotoViewHolder.timeAdded.setText(post.timeCreated);
            String content = post.getPostDescription();
            topicWithPhotoViewHolder.mMediumTextView.setText(content);
            topicWithPhotoViewHolder.commentCount.setText(
                    post.commentCount > 1 ? post.commentCount + " Responses" : post.commentCount + " Response"
            );
            topicWithPhotoViewHolder.comment.setColorFilter(ContextCompat.getColor(mContext, R.color.textColorSecondary));
            if(post.bookmarked) {
                topicWithPhotoViewHolder.addToBookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
            }else {
                topicWithPhotoViewHolder.addToBookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
            }
            topicWithPhotoViewHolder.addToBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(post.bookmarked) {
                        BookMark bookMark = new BookMark();
                        bookMark.mPostID = post.postID;

                        mDatabaseManager.deleteBookmark(bookMark);
                        post.bookmarked = false;
                    }else {
                        BookMark bookMark = new BookMark();
                        bookMark.mPostID = post.postID;
                        bookMark.JSON = post.rawPost();

                        mDatabaseManager.bookmark(bookMark);
                        post.bookmarked = true;
                    }
                    notifyItemChanged(position);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPostList.get(position).hasPhoto() ? ROW_TYPE_IMAGE : ROW_TYPE_TOPIC;
    }

    class TopicViewHolder extends RecyclerView.ViewHolder {

        public TopicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    class OrdinaryTopicViewHolder extends TopicViewHolder {

        @BindView(R.id.op_iv_post_item)
        CircleImageView circleImageView;
        @BindView(R.id.op_name_tv_poost_item)
        TextView opName;
        @BindView(R.id.post_time_added_tv_post_item)
        TextView timePosted;
        @BindView(R.id.tv_post_title_post_item)
        TextView postTitle;
        @BindView(R.id.btn_comment_post_item)
        ImageButton comment;
        @BindView(R.id.tv_comment_count_post_item)
        TextView commentCount;
        @BindView(R.id.html_tv_post_item)
        HtmlTextView mHtmlTextView;
        @BindView(R.id.add_to_bookmark_post_item)
        ImageButton addToBookmark;


        public OrdinaryTopicViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = mPostList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, TopicDetailsActivity.class);
                    intent.putExtra("raw_post", post.rawPost());
                    mContext.startActivity(intent);
                }
            });
        }
    }
    class TopicWithPhotoViewHolder extends TopicViewHolder {

        @BindView(R.id.post_iv_post_item_with_image)
        ImageView mImageView;
        @BindView(R.id.op_iv_post_item_with_image)
        CircleImageView circleImageView;
        @BindView(R.id.op_name_tv_poost_item_with_image)
        TextView opName;
        @BindView(R.id.tv_post_time_created_with_image)
        TextView timeAdded;
        @BindView(R.id.post_title_tv_post_with_image)
        TextView titleTextView;
        @BindView(R.id.post_content_tv_post_item_post_with_image)
        HtmlTextView mMediumTextView;
        @BindView(R.id.btn_comment_post_item_with_image)
        ImageButton comment;
        @BindView(R.id.comment_count_tv_post_item_with_image)
        TextView commentCount;
        @BindView(R.id.btn_add_to_bookmark_post_item_with_image)
        ImageButton addToBookmark;

        public TopicWithPhotoViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    Post post = mPostList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, TopicDetailsActivity.class);
                    intent.putExtra("raw_post", post.rawPost());
                    mContext.startActivity(intent);
                }
            });
        }
    }
    public void add(List<Post> posts, int from) {
        for (Post post : posts) {
            mPostList.add(post);
        }
        notifyItemRangeChanged(from, (mPostList.size() - 1));
    }
}
