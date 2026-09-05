package com.jackson.smartsleepalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

final class Scheduler {
    static final String PREFS = "alarm_settings", ENABLED = "enabled", NEXT = "next_alarm";
    static final String WAKE_HOUR = "wake_hour", WAKE_MINUTE = "wake_minute", SLEEP_MINUTES = "sleep_minutes";
    static final int MONITOR_ID = 10, ALARM_ID = 11;
    private Scheduler() {}

    static boolean enabled(Context c) { return c.getSharedPreferences(PREFS,0).getBoolean(ENABLED,true); }
    static int wakeHour(Context c) { return c.getSharedPreferences(PREFS,0).getInt(WAKE_HOUR,8); }
    static int wakeMinute(Context c) { return c.getSharedPreferences(PREFS,0).getInt(WAKE_MINUTE,15); }
    static int sleepMinutes(Context c) { return c.getSharedPreferences(PREFS,0).getInt(SLEEP_MINUTES,510); }
    static AlarmManager manager(Context c) { return (AlarmManager)c.getSystemService(Context.ALARM_SERVICE); }
    static PendingIntent monitorIntent(Context c) {
        return PendingIntent.getBroadcast(c,MONITOR_ID,new Intent(c,MonitorReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    static PendingIntent alarmIntent(Context c) {
        return PendingIntent.getBroadcast(c,ALARM_ID,new Intent(c,AlarmReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    static void exact(Context c,long at,PendingIntent pi) {
        AlarmManager am=manager(c);
        if(android.os.Build.VERSION.SDK_INT<31 || am.canScheduleExactAlarms()) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pi);
        else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pi);
    }
    static void scheduleMonitor(Context c,long at) { exact(c,at,monitorIntent(c)); }
    static void scheduleNextWindow(Context c) {
        Calendar q=Calendar.getInstance(); q.set(Calendar.HOUR_OF_DAY,21);q.set(Calendar.MINUTE,0);q.set(Calendar.SECOND,0);q.set(Calendar.MILLISECOND,0);
        if(q.getTimeInMillis()<=System.currentTimeMillis())q.add(Calendar.DAY_OF_YEAR,1);
        scheduleMonitor(c,q.getTimeInMillis());
    }
    static void scheduleWake(Context c,long at) {
        manager(c).cancel(alarmIntent(c)); exact(c,at,alarmIntent(c));
        c.getSharedPreferences(PREFS,0).edit().putLong(NEXT,at).apply();
    }
    static void cancelAll(Context c) {
        manager(c).cancel(monitorIntent(c));manager(c).cancel(alarmIntent(c));
        c.getSharedPreferences(PREFS,0).edit().remove(NEXT).apply();
    }
    static String formattedNext(Context c) {
        long v=c.getSharedPreferences(PREFS,0).getLong(NEXT,0);
        return v==0?"等待今晚检测":DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT).format(new Date(v));
    }
}
