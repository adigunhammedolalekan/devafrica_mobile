package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.core.Requests;
import com.beem24.projects.devafrica.entities.Queue;
import com.beem24.projects.devafrica.entities.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/8/2017.
 * Beem24, Inc
 */

public class FollowUserAdapter extends RecyclerView.Adapter<FollowUserAdapter.FollowUserViewHolder> {

    private List<User> mData;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private String mCurrentUserID = "";

    public FollowUserAdapter(Context context, List<User> users) {
        mData = users;
        mContext = context;
        if(mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);

        mCurrentUserID = PreferenceManager.getInstance().getUserID();
        for (int i = 0; i < mData.size(); i++) {
            User next = mData.get(i);
            if(TextUtils.equals(mCurrentUserID, next.ID)) {
                mData.remove(i);
                break;
            }
        }
    }
    @Override
    public FollowUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
        return new FollowUserViewHolder(mLayoutInflater.inflate(R.layout.layout_user, parent, false));
    }

    @Override
    public void onBindViewHolder(FollowUserViewHolder holder, int position) {

        final int idx = position;
        final User user = mData.get(position);
        if(!user.mPhoto.isEmpty()) {
            Glide.with(mContext).load(user.mPhoto)
                    .placeholder(R.color.divider)
                    .error(R.color.divider).dontAnimate().into(holder.circleImageView);
        }else {
            Glide.with(mContext).load(R.color.divider).
                    placeholder(R.color.divider).dontAnimate().into(holder.circleImageView);
        }
        if(user.selected) {
            holder.follow.setText("FOLLOWING");
            holder.follow.setBackgroundResource(R.drawable.btn_bg_green);
            holder.follow.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        }else {
            holder.follow.setText("FOLLOW");
            holder.follow.setBackgroundResource(R.drawable.btn_bg);
            holder.follow.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
        holder.bio.setText(user.bio);
        holder.username.setText(user.username);

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user.selected) {
                    new AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                            .setTitle("UnFollow " + user.username).setMessage("Do you really want to unFollow "+user.username + "?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    user.selected = false;
                                    Queue queue = new Queue();
                                    queue.mURL = Requests.BASE_URL + "/user/unfollow";
                                    queue.mRequestParams.put("user_id", mCurrentUserID);
                                    queue.mRequestParams.put("master_id", user.ID);
                                    DevAfrica.getApp().addJob(queue);
                                    notifyItemChanged(idx);
                                }
                            }).setNegativeButton("CANCEL", null).create().show();
                }else {
                    user.selected = true;
                    Queue queue = new Queue();
                    queue.mURL = Requests.BASE_URL + "/user/follow";
                    queue.mRequestParams.put("user_id", mCurrentUserID);
                    queue.mRequestParams.put("master_id", user.ID);
                    DevAfrica.getApp().addJob(queue);
                    notifyItemChanged(idx);
                }

            }
        });

    }

    public List<User> getSelected() {
        List<User> users = new ArrayList<>();

        for (User user : mData)
            if (user.selected)
                users.add(user);

        return users;
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    class FollowUserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_layout_user)
        CircleImageView circleImageView;
        @BindView(R.id.tv_username_layout_user)
        TextView username;
        @BindView(R.id.tv_bio_user_layout)
        TextView bio;
        @BindView(R.id.btn_follow)
        Button follow;

        public FollowUserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
