package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.Comment;
import com.beem24.projects.devafrica.entities.Notification;
import com.beem24.projects.devafrica.entities.NotificationGroup;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.activities.ActivityProfile;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/18/2017.
 * Beem24, Inc
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> mNotificationGroups;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public NotificationAdapter(Context context, List<Notification> notificationGroups) {
        this(context);
        mNotificationGroups = notificationGroups;

    }
    public NotificationAdapter(Context context) {
        mContext = context;
        if(context != null)
            mLayoutInflater = LayoutInflater.from(mContext);
    }
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
        return new NotificationViewHolder(mLayoutInflater.inflate(R.layout.layout_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        final Notification notificationGroup = mNotificationGroups.get(position);
        String text = "";
        switch (notificationGroup.getType()) {
            case Notification.NEW_COMMENT:
                Comment comment = notificationGroup.getComment();
                text = "<b>" + comment.mUser.username + "</b> responded to your post ";
                text += "<b style='text-size:20px'>'" + comment.postTitle + "'</b>";
                holder.notifTextView.setText(Html.fromHtml(text));
                holder.notifTimeTextView.setText(comment.timePosted);
                if(!comment.mUser.mPhoto.isEmpty()) {
                    Glide.with(mContext).load(comment.mUser.mPhoto)
                            .error(R.color.divider).placeholder(R.color.divider).dontAnimate()
                            .into(holder.circleImageView);
                }else {
                    Glide.with(mContext).load(R.color.divider)
                            .error(R.color.divider).placeholder(R.color.divider).dontAnimate()
                            .into(holder.circleImageView);
                }
                break;
            case Notification.NEW_FOLLOWER:
                User user = notificationGroup.getFollower();
                text = "<b>" + user.username + "</b> started following you.";
                holder.notifTextView.setText(Html.fromHtml(text));

                if(!user.mPhoto.isEmpty()) {
                    Glide.with(mContext).load(user.mPhoto)
                            .error(R.color.divider).placeholder(R.color.divider)
                            .into(holder.circleImageView);
                }else {
                    Glide.with(mContext).load(R.color.divider)
                            .error(R.color.divider).placeholder(R.color.divider)
                            .into(holder.circleImageView);
                }
                break;
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (notificationGroup.getType()) {
                    case Notification.NEW_COMMENT:
                        break;
                    case Notification.NEW_FOLLOWER:
                        User user = notificationGroup.getFollower();
                        Intent intent = new Intent(mContext, ActivityProfile.class);
                        intent.putExtra("raw_user", user.raw());
                        mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotificationGroups.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_notification)
        CircleImageView circleImageView;
        @BindView(R.id.tv_notification_text)
        TextView notifTextView;
        @BindView(R.id.tv_notification_time)
        TextView notifTimeTextView;
        @BindView(R.id.layout_notification_root)
        RelativeLayout root;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
