package com.autopai.music.entry;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.autopai.music.bean.MusicInfo;
import com.autopai.music.R;
import com.autopai.music.utils.LogUtil;
import com.autopai.music.utils.MusicUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.VH> {
    private final static String TAG = "RecyclerViewAdapter";
    private List<MusicInfo> mList;
    private Activity mActivity;
    public static class VH extends RecyclerView.ViewHolder {
        public TextView mTitleText;
        public TextView mFormatText;
        public TextView mPathText;

        public VH(View view) {
            super(view);
            mFormatText = view.findViewById(R.id.format);
            mTitleText = view.findViewById(R.id.title);
            mPathText = view.findViewById(R.id.path);
        }
    }
    public RecyclerViewAdapter() {
        mList = getFilelist();
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH viewHolder, final int position) {
        LogUtil.e(TAG,"onBindViewHolder");
        viewHolder.mTitleText.setText(mList.get(position).title);
        viewHolder.mFormatText.setText(mList.get(position).format);
        viewHolder.mPathText.setText(mList.get(position).path);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MusicApp.getInstance(),PlayMusicActivity.class);
                LogUtil.i(TAG,"MainActivity onClick mList.get(position).path="
                        + mList.get(position).path);
                intent.putExtra(PlayMusicActivity.PATH, mList.get(position).path);
                if (mActivity != null) {
                    mActivity.startActivity(intent);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MusicApp.getInstance().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }

    public void bindActivity(Activity activity) {
        mActivity = activity;
    }

    private List<MusicInfo> getFilelist() {
        List<MusicInfo> list = new ArrayList<>();
        File fileParent = new File(MainActivity.FILE_PATH);
        File[] files = fileParent.listFiles();
        if (files == null) {
            LogUtil.i(TAG,"getFilelist files == null");
            return list;
        }
        for(File file : files) {
            String format = MusicUtil.getFormatName(file.getPath());
            if (!MusicUtil.isMusic(format)) {
                LogUtil.i(TAG,"getFilelist continue format=" +format);
                continue;
            }
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.path = file.getPath();
            musicInfo.title = file.getName();
            musicInfo.format = format;
            list.add(musicInfo);
        }
        return list;
    }
}
