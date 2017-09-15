package com.beem24.projects.devafrica.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.beem24.projects.devafrica.R;
import com.beem24.projects.devafrica.ui.adapters.LanguageListAdapter;

import butterknife.BindView;

/**
 * Created By Adigun Hammed Olalekan
 * 7/18/2017.
 * Beem24, Inc
 */

public class ActivitySelectStack extends BaseActivity {

    @BindView(R.id.stack_list_rv)
    RecyclerView mRecyclerView;

    LanguageListAdapter languageListAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_stack_activity);

        languageListAdapter = new LanguageListAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(languageListAdapter);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                String selected = languageListAdapter.getSelected();
                Intent intent = getIntent();
                intent.putExtra("_selected_", selected);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
