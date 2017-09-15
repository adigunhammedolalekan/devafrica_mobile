package com.beem24.projects.devafrica.ui.views;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beem24.projects.devafrica.R;

/**
 * Created By Adigun Hammed Olalekan
 * 7/9/2017.
 * Beem24, Inc
 */

public class BadgeTabLayout extends TabLayout {

    //a custom tablayout that support badge

    private SparseArray<Builder> mBuilders = new SparseArray<>();
    public BadgeTabLayout(Context context) {
        super(context);
    }

    public BadgeTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BadgeTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Builder with(int pos) {
        Tab tab = getTabAt(pos);
        return with(tab);
    }
    public Builder with(Tab tab) {

        Builder builder = mBuilders.get(tab.getPosition());
        if(builder == null) {
            builder = new Builder(this, tab);
            mBuilders.put(tab.getPosition(), builder);
        }
        return builder;
    }
    public static final class Builder {

        private int mCurrentTabCount = 0;
        private View customView;
        private TextView badge;
        private ImageView icon;
        private Context mContext;
        private TabLayout.Tab mTab;
        private int mIconRes;
        private boolean hasBadge = false;

        private Builder(TabLayout tabLayout, TabLayout.Tab tab) {
            super();
            mContext = tabLayout.getContext();
            mTab = tab;

            customView = LayoutInflater.from(mContext).inflate(R.layout.layout_tab_item, tabLayout, false);
            badge = (TextView) customView.findViewById(R.id.notif_badge);
            icon = (ImageView) customView.findViewById(R.id.tab_icon);

        }
        public Builder icon(int icon) {
            mIconRes = icon;
            return this;
        }
        public Builder badgeCount(int count) {
            mCurrentTabCount = count;
            return this;
        }
        public Builder decrease() {
            mCurrentTabCount -= 1;
            badge.setText(mCurrentTabCount > 100 ? "99+" : String.valueOf(mCurrentTabCount));
            return this;
        }
        public Builder increase() {
            mCurrentTabCount += 1;
            badge.setText(mCurrentTabCount > 100 ? "99+" : String.valueOf(mCurrentTabCount));
            return this;
        }
        public Builder withBadge() {
            hasBadge = true;
            return this;
        }
        public void build() {
            if(customView == null)
                return;

            icon.setImageResource(mIconRes);
            icon.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
            if(hasBadge) {
                badge.setText(mCurrentTabCount > 100 ? "99+" : String.valueOf(mCurrentTabCount));
                badge.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                badge.setVisibility(VISIBLE);
            }
            mTab.setCustomView(customView);
        }
    }
}
