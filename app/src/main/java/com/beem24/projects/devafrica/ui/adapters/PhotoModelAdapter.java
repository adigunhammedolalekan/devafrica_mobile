package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.Photo;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By Adigun Hammed Olalekan
 * 7/8/2017.
 * Beem24, Inc
 */

public class PhotoModelAdapter extends RecyclerView.Adapter<PhotoModelAdapter.PhotoModelViewHolder>{

    private LayoutInflater mLayoutInflater;
    private List<Photo> mPhotos;
    private Context mContext;

    public PhotoModelAdapter(Context context, List<Photo> photos) {
        mPhotos = photos;
        mContext = context;
        if(mContext != null)
            mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public PhotoModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(mContext);
        return new PhotoModelViewHolder(mLayoutInflater.inflate(R.layout.layout_photo_model, parent, false));
    }

    @Override
    public void onBindViewHolder(PhotoModelViewHolder holder, int position) {
        final int idx = position;
        String next = mPhotos.get(position).mPath;
        Glide.with(mContext)
                .load(new File(next)).error(R.color.divider)
                .placeholder(R.color.divider).dontAnimate().into(holder.iv);

        holder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotos.remove(idx);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    class PhotoModelViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ic_clear_photo_photo_model)
        ImageButton clear;
        @BindView(R.id.iv_photo_photo_model)
        ImageView iv;

        public PhotoModelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
