package com.autopai.music.entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.autopai.music.R;
import com.autopai.music.utils.LogUtil;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Button mButton;
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/Music";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Debug.startMethodTracing("Music");
        mButton = findViewById(R.id.RefreshButton);
        mButton.setText("扫描完跳转到歌曲列表界面");
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, NativeSongActivity.class);
                startActivity(intent);
            }
        });
        initRecyclerView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Debug.stopMethodTracing();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.RecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mRecyclerViewAdapter.bindActivity(this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

}
