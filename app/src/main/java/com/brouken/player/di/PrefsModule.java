package com.brouken.player.di;

import android.content.Context;

import com.brouken.player.Prefs;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
final class PrefsModule {

    @Provides
    @Singleton
    static Prefs providePrefs(@ApplicationContext Context context) {
        return new Prefs(context);
    }
}
