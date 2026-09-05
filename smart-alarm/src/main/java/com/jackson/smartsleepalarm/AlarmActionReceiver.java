package com.jackson.smartsleepalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class AlarmActionReceiver extends BroadcastReceiver {
    static final String ACTION_STOP="com.jackson.smartsleepalarm.STOP";
    static final String ACTION_SNOOZE="com.jackson.smartsleepalarm.SNOOZE";
    @Override public void onReceive(Context c,Intent i){
        c.stopService(new Intent(c,AlarmService.class));
        if(ACTION_SNOOZE.equals(i.getAction())) Scheduler.scheduleWake(c,System.currentTimeMillis()+5*60_000L);
        else { c.getSharedPreferences(Scheduler.PREFS,0).edit().remove(Scheduler.NEXT).apply();Scheduler.scheduleNextWindow(c); }
    }
}
