package com.jackson.smartsleepalarm;
import android.content.BroadcastReceiver;import android.content.Context;import android.content.Intent;
public final class AlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent i){c.startForegroundService(new Intent(c,AlarmService.class));}
}
