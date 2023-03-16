package com.brouken.player.screens.secure.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.brouken.player.screens.secure.view.FolderAdapter;
import com.brouken.player.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;

@HiltViewModel
public class SecureFolderViewModel extends ViewModel {

    private MutableLiveData<List<File>> _files = new MutableLiveData<>();
    public LiveData<List<File>> files = _files;
    private MutableLiveData<Boolean> _authenticated = new MutableLiveData<>(false);
    public LiveData<Boolean> authenticated = _authenticated;
    private Context mContext;

    @Inject
    public SecureFolderViewModel() {
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setAuthenticated(boolean authenticated) {
        _authenticated.postValue(authenticated);
    }

    public void load(File file) {
        File[] listFile = file.listFiles();
        if (listFile != null && listFile.length != 0)
            _files.postValue(Arrays.asList(listFile));
    }
}
