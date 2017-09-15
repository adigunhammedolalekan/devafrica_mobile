package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.Comment;
import com.beem24.projects.devafrica.ui.activities.PostReplyActivity;
import com.bumptech.glide.Glide;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/8/2017.
 * Beem24, Inc
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<Comment> mComments;
    private Context mContext;
    private boolean mIgnoreReply;

    private ICommentMenuItemClickListener iCommentMenuItemClickListener;

    public CommentAdapter(Context context, List<Comment> comments, boolean ignoreReply) {
        mComments = comments;
        mContext = context;
        if(mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);
        mIgnoreReply = ignoreReply;
    }
    public void addListener(ICommentMenuItemClickListener commentMenuItemClickListener) {
        iCommentMenuItemClickListener = commentMenuItemClickListener;
    }
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
        return new CommentViewHolder(mLayoutInflater.inflate(R.layout.layout_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {

        final Comment comment = mComments.get(position);
        String replyText = comment.replyCount > 1 ? comment.replyCount + " Replies." : comment.replyCount + " Reply.";
        holder.replyCount.setText(replyText);
        if(!comment.mUser.mPhoto.isEmpty()) {
            Glide.with(mContext).load(comment.mUser.mPhoto)
                    .placeholder(R.color.divider)
                    .error(R.color.divider).dontAnimate().into(holder.dp);
        }
        holder.mHtmlTextView.setHtml(comment.commentBody);
        holder.username.setText(comment.mUser.username);
        holder.timePosted.setText(comment.timePosted);
        if(mIgnoreReply) {
            holder.replyRoot.setVisibility(View.GONE);
        }
        if(!mIgnoreReply) {
            holder.commentRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PostReplyActivity.class);
                    intent.putExtra("raw_comment", comment.raw());
                    mContext.startActivity(intent);
                }
            });
        }
        holder.replyRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostReplyActivity.class);
                intent.putExtra("raw_comment", comment.raw());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_layout_comment)
        RelativeLayout commentRoot;
        @BindView(R.id.user_iv_comment_item)
        CircleImageView dp;
        @BindView(R.id.username_comment_item)
        TextView username;
        @BindView(R.id.comment_time_posted_comment_item)
        TextView timePosted;
        @BindView(R.id.btn_more_comment_item)
        ImageButton more;
        @BindView(R.id.comment_content_tv)
        HtmlTextView mHtmlTextView;
        @BindView(R.id.reply_count_tv_comment)
        TextView replyCount;
        @BindView(R.id.comment_reply_root)
        RelativeLayout replyRoot;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu(more, getAdapterPosition());
                }
            });
        }
    }
    void showMenu(View view, int position) {
        final int idx = position;
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.pop_up_menu_comment_item, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_view_profile:
                        if(iCommentMenuItemClickListener != null)
                            iCommentMenuItemClickListener.onViewProfileClick(idx);
                        break;
                    default:break;
                }
                return true;
            }
        });
        popupMenu.show();
    }
    public interface ICommentMenuItemClickListener {

        void onReportClick(int position);
        void onMentionClick(int position);
        void onViewProfileClick(int position);

    }
}
