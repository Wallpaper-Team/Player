package com.brouken.player.screens.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.brouken.player.R;
import com.brouken.player.screens.home.model.MainOptionMenuItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<MainOptionMenuItem>> _options = new MutableLiveData<>();
    public LiveData<List<MainOptionMenuItem>> options = _options;

    @Inject
    public HomeViewModel() {
        loadOptions();
    }

    public void loadOptions() {
        List<MainOptionMenuItem> list = new ArrayList<>();
        list.add(new MainOptionMenuItem(R.mipmap.ic_launcher, R.string.app_name, R.string.exo_download_description));
        list.add(new MainOptionMenuItem(R.mipmap.ic_launcher, R.string.app_name, R.string.exo_download_description));
        list.add(new MainOptionMenuItem(R.mipmap.ic_launcher, R.string.app_name, R.string.exo_download_description));
        list.add(new MainOptionMenuItem(R.mipmap.ic_launcher, R.string.app_name, R.string.exo_download_description));
        list.add(new MainOptionMenuItem(R.mipmap.ic_launcher, R.string.app_name, R.string.exo_download_description));
        list.add(new MainOptionMenuItem(R.mipmap.ic_launcher, R.string.app_name, R.string.exo_download_description));
        _options.postValue(list);
    }
}
