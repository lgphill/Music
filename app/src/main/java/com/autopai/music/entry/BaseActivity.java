package com.autopai.music.entry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.autopai.music.R;
import com.autopai.music.utils.LogUtil;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initBaseView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initBaseView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initBaseView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            finish();
            return true;
        } else if(id == R.id.action_settings) {
            startActivityByClassName(SettingsActivity.class);
        } else if(id == R.id.action_scan) {
            startActivityByClassName(ScanActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }

    private void startActivityByClassName(Class className) {
        Intent intent = new Intent();
        intent.setClass(this, className);
        startActivity(intent);
    }
    private void initBaseView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LogUtil.i(TAG, "");
    }
}
