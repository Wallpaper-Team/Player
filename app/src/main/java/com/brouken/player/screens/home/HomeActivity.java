package com.brouken.player.screens.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.brouken.player.screens.player.PlayerActivity;
import com.brouken.player.databinding.ActivityHomeBinding;
import com.brouken.player.screens.home.view.MainOptionAdapter;
import com.brouken.player.screens.home.viewmodel.HomeViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding mBinding;
    private HomeViewModel mHomeViewModel;
    @Inject
    MainOptionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mHomeViewModel.options.observe(this, options -> {
            mAdapter.setOptions(options);
        });
        mBinding.setAdapter(mAdapter);
        mBinding.fbt.setOnClickListener(v -> {
            startActivity(new Intent(this, PlayerActivity.class));
        });
    }
}