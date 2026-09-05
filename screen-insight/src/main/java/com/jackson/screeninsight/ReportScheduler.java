package com.jackson.screeninsight;

import android.app.AlarmManager;import android.app.PendingIntent;import android.content.Context;import android.content.Intent;import java.util.Calendar;

final class ReportScheduler {
    static final String PREFS="report_settings",DAY="day",HOUR="hour",MINUTE="minute",ENABLED="enabled";
    static void schedule(Context c){if(!c.getSharedPreferences(PREFS,0).getBoolean(ENABLED,true))return;Calendar q=Calendar.getInstance();int day=c.getSharedPreferences(PREFS,0).getInt(DAY,Calendar.SUNDAY),h=c.getSharedPreferences(PREFS,0).getInt(HOUR,19),m=c.getSharedPreferences(PREFS,0).getInt(MINUTE,0);q.set(Calendar.DAY_OF_WEEK,day);q.set(Calendar.HOUR_OF_DAY,h);q.set(Calendar.MINUTE,m);q.set(Calendar.SECOND,0);q.set(Calendar.MILLISECOND,0);if(q.getTimeInMillis()<=System.currentTimeMillis())q.add(Calendar.WEEK_OF_YEAR,1);AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,q.getTimeInMillis(),intent(c));}
    static void cancel(Context c){((AlarmManager)c.getSystemService(Context.ALARM_SERVICE)).cancel(intent(c));}
    private static PendingIntent intent(Context c){return PendingIntent.getBroadcast(c,90,new Intent(c,ReportReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);}
}
