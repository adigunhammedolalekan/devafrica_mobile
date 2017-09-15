package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.Comment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/8/2017.
 * Beem24, Inc
 */

public class PostReplyAdapter extends RecyclerView.Adapter<PostReplyAdapter.PostReplyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Comment> comments;

    public PostReplyAdapter(Context context, List<Comment> commentList) {
        mContext = context;
        comments = commentList;
        if(mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);

    }
    @Override
    public PostReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
        return new PostReplyViewHolder(mLayoutInflater.inflate(R.layout.layout_reply, parent, false));
    }

    @Override
    public void onBindViewHolder(PostReplyViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        holder.content.setText(comment.commentBody);
        holder.usernameTextView.setText(comment.mUser.username);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class PostReplyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_reply_item)
        CircleImageView dp;
        @BindView(R.id.tv_username_reply_item)
        TextView usernameTextView;
        @BindView(R.id.tv_reply_content_reply_item)
        TextView content;

        public PostReplyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
