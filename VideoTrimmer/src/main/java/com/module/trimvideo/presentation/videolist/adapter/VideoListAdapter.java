package com.module.trimvideo.presentation.videolist.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.module.core.model.MediaData;
import com.module.trimvideo.R;
import com.module.trimvideo.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoHolder> {

    private List<MediaData> mData;
    private WeakReference<OnClickHandler> mCallback;

    public VideoListAdapter(OnClickHandler callback) {
        mData = new ArrayList<>();
        mCallback = new WeakReference<>(callback);
    }

    public void setData(List<MediaData> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private ImageView mThumbnail, mMore;
        private TextView mTitle, mContent;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            mThumbnail = itemView.findViewById(R.id.video_thumbnail);
            mMore = itemView.findViewById(R.id.video_more);
            mTitle = itemView.findViewById(R.id.video_title);
            mContent = itemView.findViewById(R.id.video_detail);
            itemView.setOnClickListener(view -> {
                if (mCallback != null) mCallback.get().handleClick(mData.get(getAdapterPosition()));
            });
            mMore.setOnClickListener(view -> {

            });
        }

        public void bind(MediaData mediaData) {
            mTitle.setText(mediaData.name);
            String content = mediaData.width + "x" + mediaData.height + " | " + Utils.convertSize(mediaData.size);
            mContent.setText(content);
            Glide.with(mThumbnail).load(Uri.fromFile(new File(mediaData.path))).centerCrop().override(320, 180)
                    .placeholder(R.drawable.ic_image).into(mThumbnail);
        }
    }
}
