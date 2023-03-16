package com.brouken.player.screens.secure.view;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brouken.player.databinding.FileItem1Binding;
import com.brouken.player.utils.ThumbnailWrapper;

import java.io.File;
import java.io.IOException;
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
        FileItem1Binding binding = FileItem1Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private FileItem1Binding mBinding;

        public FolderViewHolder(FileItem1Binding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(File item) {
            mBinding.setItem(item);
            itemView.setOnClickListener(v -> listener.onClick(item));
            if (item.isFile()) {
                Bitmap thumb;
                try {
                    thumb = ThumbnailWrapper.createThumbnail(itemView.getContext(), item, 100, 100);
                    mBinding.icon.setImageBitmap(thumb);
                } catch (IOException e) {
                }
            }
        }
    }

    public interface ItemOnClickListener {
        void onClick(File item);
    }
}
