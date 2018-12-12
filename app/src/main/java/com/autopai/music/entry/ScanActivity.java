package com.autopai.music.entry;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import com.autopai.music.R;
import com.autopai.music.scan.ScanService;
import com.autopai.music.utils.LogUtil;

public class ScanActivity extends BaseActivity {
    private static final String TAG = "ScanActivity";
    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");
        setContentView(R.layout.activity_scan);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i(TAG, "onClick");
                Intent intent = new Intent();
                intent.setClass(ScanActivity.this, ScanService.class);
                startService(intent);
            }
        });
    }
}
