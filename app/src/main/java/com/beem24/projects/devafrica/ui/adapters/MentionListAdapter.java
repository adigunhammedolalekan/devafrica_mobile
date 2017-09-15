package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.Follower;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/20/2017.
 * Beem24, Inc
 */

public class MentionListAdapter extends RecyclerView.Adapter<MentionListAdapter.MentionListViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Follower> mentionAble;
    private IMentionClickListener mentionClickListener;

    public MentionListAdapter(Context context, IMentionClickListener iMentionClickListener) {
        mentionAble = new ArrayList<>();
        mContext = context;
        if(mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);
        mentionClickListener = iMentionClickListener;
    }
    public void add(Follower follower) {
        mentionAble.add(follower);
        notifyDataSetChanged();
    }
    public void remove(int idx) {
        try {
            mentionAble.remove(idx);
        }catch (Exception e) {}
    }

    @Override
    public MentionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
        return new MentionListViewHolder(mLayoutInflater.inflate(R.layout.mention, parent, false));
    }

    @Override
    public void onBindViewHolder(MentionListViewHolder holder, int position) {
        final Follower follower = mentionAble.get(position);

        holder.username.setText(follower.mUsername);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mentionClickListener != null)
                    mentionClickListener.onMentionClick(follower.mUsername);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mentionAble.size();
    }

    class MentionListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_mention)
        CircleImageView circleImageView;
        @BindView(R.id.username_tv_mention)
        TextView username;
        @BindView(R.id.mention_root)
        CardView mCardView;

        public MentionListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    public interface IMentionClickListener {

        void onMentionClick(String name);

    }
}
