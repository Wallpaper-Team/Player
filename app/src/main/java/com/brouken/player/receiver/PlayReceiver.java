package com.brouken.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.brouken.player.screens.player.PlayerActivity;

import java.io.File;

public class PlayReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String uri = intent.getStringExtra("Uri");
        Intent intent1 = new Intent(context, PlayerActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.setData(Uri.fromFile(new File(uri)));
        context.startActivity(intent1);
    }
}
