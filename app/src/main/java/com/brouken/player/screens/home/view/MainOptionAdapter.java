package com.brouken.player.screens.home.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brouken.player.databinding.MainOptionItemBinding;
import com.brouken.player.screens.home.model.MainOptionMenuItem;

import java.util.List;

import javax.inject.Inject;

public class MainOptionAdapter extends RecyclerView.Adapter<MainOptionAdapter.MainOptionViewHolder> {

    private List<MainOptionMenuItem> options;

    @Inject
    public MainOptionAdapter() {
    }

    public void setOptions(List<MainOptionMenuItem> options) {
        this.options = options;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MainOptionItemBinding binding = MainOptionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MainOptionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MainOptionViewHolder holder, int position) {
        MainOptionMenuItem option = options.get(position);
        holder.bind(option);
    }

    @Override
    public int getItemCount() {
        return options == null ? 0 : options.size();
    }

    class MainOptionViewHolder extends RecyclerView.ViewHolder {
        private MainOptionItemBinding mBinding;

        public MainOptionViewHolder(MainOptionItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(MainOptionMenuItem option) {
            mBinding.setOption(option);
        }
    }
}
