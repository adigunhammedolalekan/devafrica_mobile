package com.beem24.projects.devafrica.ui.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beem24.projects.devafrica.R;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By Adigun Hammed Olalekan
 * 7/5/2017.
 * Beem24, Inc
 */

public class TabRecyclerViewAdapter extends RecyclerTabLayout.Adapter<TabRecyclerViewAdapter.RecyclerTabLayoutViewHolder> {

    private List<Integer> mIcons;
    private ViewPager mViewPager;

    public TabRecyclerViewAdapter(ViewPager viewPager, List<Integer> icons) {
        super(viewPager);
        mIcons = icons;
        mViewPager = viewPager;
    }

    @Override
    public RecyclerTabLayoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerTabLayoutViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.layout_tab_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerTabLayoutViewHolder holder, int position) {
        int icon = mIcons.get(position);

        holder.icon.setImageResource(icon);
        holder.icon.setColorFilter(ContextCompat.getColor(holder.icon.getContext(), R.color.white));

        if(position == getCurrentIndicatorPosition()) {
            holder.icon.setColorFilter(ContextCompat.getColor(holder.icon.getContext(), R.color.white));
        }else{
            holder.icon.setColorFilter(ContextCompat.getColor(holder.icon.getContext(), R.color.white));
        }
        if(position == 4) {
            holder.badge.setVisibility(View.VISIBLE);
        }else {
            holder.badge.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mIcons.size();
    }

    class RecyclerTabLayoutViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tab_icon)
        ImageView icon;
        @BindView(R.id.notif_badge)
        TextView badge;

        public RecyclerTabLayoutViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getViewPager().setCurrentItem(getAdapterPosition());
                }
            });
        }
    }
}
