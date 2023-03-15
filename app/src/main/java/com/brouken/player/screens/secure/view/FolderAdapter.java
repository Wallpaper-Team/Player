package com.brouken.player.screens.secure.view;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brouken.player.databinding.FileItemBinding;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<File> items;
    private ItemOnClickListener listener;

    @Inject
    public FolderAdapter() {
    }

    public void setListener(ItemOnClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<File> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FileItemBinding binding = FileItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FolderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        File item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {
        private FileItemBinding mBinding;

        public FolderViewHolder(FileItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(File item) {
            mBinding.setItem(item);
            itemView.setOnClickListener(v -> listener.onClick(item));
            if (item.isFile()) {
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(item.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                mBinding.icon.setImageBitmap(thumb);
            }
        }
    }

    public interface ItemOnClickListener {
        void onClick(File item);
    }
}
