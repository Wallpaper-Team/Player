package com.brouken.player.screens.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.brouken.player.PlayerActivity;
import com.brouken.player.R;
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
            Log.d("Ducky", "onCreate: " + options.size());
            mAdapter.setOptions(options);
        });
        mBinding.setAdapter(mAdapter);
    }
}