package com.example.file_explorer.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.view.ViewCompat;

import com.github.axet.androidlibrary.widgets.OpenFileDialog;
import com.github.axet.androidlibrary.widgets.ThemeUtils;
import com.example.file_explorer.R;
import com.example.file_explorer.app.FilesApplication;
import com.example.file_explorer.app.Storage;

import java.util.ArrayList;
import java.util.List;

public class PathView extends HorizontalScrollView {
    Uri uri;
    LinearLayoutCompat ll;
    public Listener listener;
    TextView free;

    public interface Listener {
        void onUriSelected(Uri u);
    }

    public PathView(Context context) {
        super(context);
        create();
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        create();
    }

    @TargetApi(11)
    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        create();
    }

    @TargetApi(21)
    public PathView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        create();
    }

    void create() {
        ll = new LinearLayoutCompat(getContext());
        ll.setOrientation(LinearLayoutCompat.HORIZONTAL);
        addView(ll);
        loadBlackList(getContext());
    }

    public void setUri(Uri u) {
        uri = u;
        ll.removeAllViews();
        add(u);
        post(new Runnable() {
            @Override
            public void run() {
                PathView.this.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    private static final List<String> BLACK_LIST_URI = new ArrayList<>();

    void loadBlackList(Context context) {
        BLACK_LIST_URI.add(context.getApplicationContext().getExternalFilesDir("Secure").getPath());
        BLACK_LIST_URI.add(context.getExternalFilesDir("Trim").getPath());
        BLACK_LIST_URI.add(Environment.getExternalStorageDirectory().getPath());
    }

    void add(Uri uri) {
        int p15 = ThemeUtils.dp2px(getContext(), 15);
        int p10 = ThemeUtils.dp2px(getContext(), 10);
        while (uri != null) {
            AppCompatTextView b = new AppCompatTextView(getContext());
            b.setPadding(p10, p15, p10, p15);
            String n = Storage.getName(getContext(), uri);
            if (n.isEmpty()) n = OpenFileDialog.ROOT;
            b.setText(n);
            b.setTypeface(null, Typeface.BOLD);
            final Uri u = uri;
            b.setOnClickListener(v -> listener.onUriSelected(u));
            ll.addView(b, 0);
            for (String s : BLACK_LIST_URI) {
                if (s.equals(uri.getPath())) {
                    return;
                }
            }
            uri = Storage.getParent(getContext(), uri);
            if (uri != null) {
                AppCompatTextView p = new AppCompatTextView(getContext());
                p.setText(">");
                ViewCompat.setAlpha(p, 0.5f);
                ll.addView(p, 0);
            }
        }
        /*free = new TextView(getContext());
        free.setPadding(p10, p15, p10, p15);
        ViewCompat.setAlpha(free, 0.5f);
        ll.addView(free);
        updateHeader();*/
    }

    public void updateHeader() {
        /*long f = Storage.getFree(getContext(), uri);
        free.setText("[" + getContext().getString(R.string.free_space, FilesApplication.formatSize(getContext(), f)) + "]");*/
    }
}
