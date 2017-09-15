package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.Tag;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By Adigun Hammed Olalekan
 * 7/8/2017.
 * Beem24, Inc
 */

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.TagListViewHolder> {

    private List<Tag> mTags;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public TagListAdapter(List<Tag> tags, Context context) {
        mTags = tags;
        mContext = context;
        if(mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);

    }
    @Override
    public TagListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
        return new TagListViewHolder(mLayoutInflater.inflate(R.layout.tag, parent, false));
    }

    @Override
    public void onBindViewHolder(TagListViewHolder holder, int position) {
        final int idx = position;
        final Tag tag = mTags.get(position);
        holder.name.setText(tag.tagName);

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                        .setTitle("Remove Tag")
                        .setMessage("Remove Tag #"+tag.tagName)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTags.remove(idx);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("NO", null).create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    class TagListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tag_name_tag)
        TextView name;

        public TagListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
