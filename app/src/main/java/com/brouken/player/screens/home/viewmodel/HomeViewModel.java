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
        list.add(new MainOptionMenuItem(R.drawable.multimedia, R.string.video_player, R.string.video_player_description));
        list.add(new MainOptionMenuItem(R.drawable.musical, R.string.music_player, R.string.music_player_description));
        list.add(new MainOptionMenuItem(R.drawable.trim, R.string.trim_video, R.string.trim_video_description));
        list.add(new MainOptionMenuItem(R.drawable.padlock, R.string.secure_folder, R.string.secure_folder_description));
        list.add(new MainOptionMenuItem(R.drawable.video_player, R.string.video_downloader, R.string.video_downloader_description));
        list.add(new MainOptionMenuItem(R.drawable.cogwheel, R.string.settings, R.string.settings_description));
        _options.postValue(list);
    }
}
