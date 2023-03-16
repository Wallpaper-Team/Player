package com.example.file_explorer.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.file_explorer.R;
import com.example.file_explorer.app.FilesApplication;
import com.example.file_explorer.app.SuperUser;
import com.github.axet.androidlibrary.activities.AppCompatSettingsThemeActivity;
import com.github.axet.androidlibrary.widgets.Toast;

public class SettingsActivity extends AppCompatSettingsThemeActivity {

    public static final String SUIO_ERROR = "no libsuio.so found";

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getAppTheme() {
        return FilesApplication.getTheme(this, FilesApplication.PREF_THEME, R.style.AppThemeLight, R.style.AppThemeDark);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSettingsFragment(new GeneralPreferenceFragment());
        setupActionBar();
    }

    @Override
    public String getAppThemeKey() {
        return FilesApplication.PREF_THEME;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        FileExActivity.start(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            Preference root = findPreference(FilesApplication.PREF_ROOT);
            root.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((boolean) newValue) {
                        SuperUser.trapTest();
                        SuperUser.Result r = SuperUser.rootTest();
                        if (!r.ok()) {
                            Toast.Error(getContext(), r.errno());
                            return false;
                        } else {
                            SuperUser.exitTest(); // second su invoke
                            if (SuperUser.binSuio(getContext()) == null) {
                                Toast.Text(getContext(), SUIO_ERROR);
                                return false;
                            }
                            return true;
                        }
                    }
                    return true;
                }
            });
            if (!root.isEnabled() && !SuperUser.isRooted())
                root.setVisible(false);
            bindPreferenceSummaryToValue(findPreference(FilesApplication.PREF_THEME));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
