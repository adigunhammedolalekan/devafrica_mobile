package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.Post;
import com.bumptech.glide.Glide;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By Adigun Hammed Olalekan
 * 7/9/2017.
 * Beem24, Inc
 */

public class PostAdapterImpl extends RecyclerView.Adapter<PostAdapterImpl.PostAdapterImplViewHolder> {

    public static final int ROW_WITH_IMAGE = 1;
    public static final int ROW_TEXT_ONLY = 2;

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Post> mPostList;

    public PostAdapterImpl(Context context, List<Post> posts) {
        mPostList = posts;
        mContext = context;
        if(mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);

    }
    @Override
    public PostAdapterImplViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
        if(viewType == ROW_TEXT_ONLY) {
            return new PostWithTextViewHolder(mLayoutInflater.inflate(R.layout.my_post_item, parent, false));
        }
        return new PostWithImageViewHolder(mLayoutInflater.inflate(R.layout.my_post_item_with_image, parent, false));
    }

    @Override
    public void onBindViewHolder(PostAdapterImplViewHolder holder, int position) {

        final Post post = mPostList.get(position);
        if(holder instanceof PostWithImageViewHolder) {
            PostWithImageViewHolder postWithImageViewHolder = (PostWithImageViewHolder) holder;
            postWithImageViewHolder.htmlTextView.setText(post.getPostDescription());
            Glide.with(mContext)
                    .load(post.getThumbURI()).error(R.color.divider).placeholder(R.color.divider)
                    .into(postWithImageViewHolder.imageView);
            postWithImageViewHolder.title.setText(post.postTitle);
        }else if(holder instanceof PostWithTextViewHolder) {
            PostWithTextViewHolder postWithTextViewHolder = (PostWithTextViewHolder) holder;
            postWithTextViewHolder.htmlTextView.setText(post.getPostDescription());
            postWithTextViewHolder.mtitle.setText(post.postTitle);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mPostList.get(position).hasPhoto() ? ROW_WITH_IMAGE : ROW_TEXT_ONLY;
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    class PostAdapterImplViewHolder extends RecyclerView.ViewHolder {

        public PostAdapterImplViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    class PostWithImageViewHolder extends PostAdapterImplViewHolder {

        @BindView(R.id.iv_post_my_post_item)
        ImageView imageView;
        @BindView(R.id.tv_post_title_my_post_item)
        TextView title;
        @BindView(R.id.tv_content_my_post_item)
        HtmlTextView htmlTextView;
        @BindView(R.id.tv_post_response_count_my_post_item)
        TextView responseCount;

        public PostWithImageViewHolder(View itemView) {
            super(itemView);
        }
    }
    class PostWithTextViewHolder extends PostAdapterImplViewHolder {

        @BindView(R.id.tv_post_title_mpt)
        TextView mtitle;
        @BindView(R.id.tv_post_content_mpt)
        HtmlTextView htmlTextView;
        @BindView(R.id.tv_post_response_count_mpt)
        TextView responseCount;

        public PostWithTextViewHolder(View itemView) {
            super(itemView);
        }
    }
}
