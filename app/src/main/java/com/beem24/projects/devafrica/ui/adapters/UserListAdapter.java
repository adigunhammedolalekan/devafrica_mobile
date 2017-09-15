package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.core.PreferenceManager;
import com.beem24.projects.devafrica.entities.User;
import com.beem24.projects.devafrica.ui.activities.ActivityProfile;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/20/2017.
 * Beem24, Inc
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<User> mData;

    private User currentUser;

    public UserListAdapter(List<User> data, Context context) {
        mContext = context;
        mData = data;
        if(mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);
        currentUser = PreferenceManager.getInstance().getUser();

        for (int i = 0; i < mData.size(); i++) {
            User next = mData.get(i);

            if(currentUser.ID.trim().equalsIgnoreCase(next.ID.trim())) {
                mData.remove(i);
                break;
            }
        }
    }
    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
        return new UserListViewHolder(mLayoutInflater.inflate(R.layout.layout_user_casual, parent, false));
    }

    @Override
    public void onBindViewHolder(UserListViewHolder holder, int position) {
        final int idx = position;
        final User user = mData.get(idx);

        if(!user.mPhoto.isEmpty()) {
            Glide.with(mContext)
                    .load(user.mPhoto).placeholder(R.color.divider)
                    .error(R.color.divider).dontAnimate().into(holder.dp);
        }else {
            Glide.with(mContext).load(user.mPhoto)
                    .placeholder(R.color.divider).error(R.color.divider).into(holder.dp);
        }
        holder.bio.setText(user.bio);
        holder.name.setText(user.username);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityProfile.class);
                intent.putExtra("raw_user", user.raw());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class UserListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_user_casual)
        CircleImageView dp;
        @BindView(R.id.tv_username_user_casual)
        TextView name;
        @BindView(R.id.bio_tv_user_casual)
        TextView bio;
        @BindView(R.id.root_user_casual)
        RelativeLayout root;

        public UserListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
