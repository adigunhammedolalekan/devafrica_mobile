package com.beem24.projects.devafrica.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beem24.projects.devafrica.DevAfrica;
import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.entities.Language;
import com.beem24.projects.devafrica.util.JSON;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created By Adigun Hammed Olalekan
 * 7/18/2017.
 * Beem24, Inc
 */

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.LanguageListViewHolder> {

    private List<Language> mLanguages;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public LanguageListAdapter(Context context) {
        mContext = context;
        if(mContext != null) {
            mLayoutInflater = LayoutInflater.from(mContext);
            DevAfrica.mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    mLanguages = JSON.languages(mContext);
                }
            });
        }
    }
    @Override
    public LanguageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
        return new LanguageListViewHolder(mLayoutInflater.inflate(R.layout.stack, parent, false));
    }

    @Override
    public void onBindViewHolder(LanguageListViewHolder holder, int position) {
        final int idx = position;
        final Language language = mLanguages.get(position);
        holder.name.setText(language.name);
        holder.wikiLink.setText(language.wikiLink);

        if(language.selected)
            holder.mSwitchCompat.setVisibility(View.VISIBLE);
        else
            holder.mSwitchCompat.setVisibility(View.GONE);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(language.selected)
                    language.selected = false;
                else
                    language.selected = true;
                notifyItemChanged(idx);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mLanguages == null)
            return 0;
        return mLanguages.size();
    }

    class LanguageListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_language_name_stack)
        TextView name;
        @BindView(R.id.stack_link_tv)
        TextView wikiLink;
        @BindView(R.id.iv_stack_selected)
        ImageView mSwitchCompat;
        @BindView(R.id.stack_root)
        View mView;

        public LanguageListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    public String getSelected() {
        List<Language> selectedLanguages = new ArrayList<>();

        for (Language language : mLanguages)
            if(language.selected)
                selectedLanguages.add(language);

        if(selectedLanguages.size() <= 0)
            return "";

        String data = "";
        for (Language language : selectedLanguages) {
            data += language.name + ", ";
        }

        return data.trim();
    }
}
