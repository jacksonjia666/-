package com.jackson.smartsleepalarm;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import java.util.Calendar;

public final class MonitorReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent ignored) {
        if(!Scheduler.enabled(c)){Scheduler.cancelAll(c);return;}
        long now=System.currentTimeMillis(); Calendar cal=Calendar.getInstance();int hour=cal.get(Calendar.HOUR_OF_DAY);
        boolean window=hour>=21||hour<3;
        if(!window){Scheduler.scheduleNextWindow(c);return;}
        PowerManager pm=(PowerManager)c.getSystemService(Context.POWER_SERVICE);
        if(pm.isInteractive()){
            Scheduler.manager(c).cancel(Scheduler.alarmIntent(c));
            c.getSharedPreferences(Scheduler.PREFS,0).edit().remove(Scheduler.NEXT).apply();
        } else {
            long off=findLastScreenOff(c,now);
            if(off>0 && now-off>=15*60_000L) scheduleFromSleep(c,off,now);
        }
        Scheduler.scheduleMonitor(c,now+10*60_000L);
    }

    private long findLastScreenOff(Context c,long now){
        UsageStatsManager us=(UsageStatsManager)c.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents events=us.queryEvents(now-12*60*60_000L,now);UsageEvents.Event e=new UsageEvents.Event();long last=0;
        while(events.hasNextEvent()){
            events.getNextEvent(e);
            if(e.getEventType()==UsageEvents.Event.SCREEN_NON_INTERACTIVE)last=e.getTimeStamp();
            else if(e.getEventType()==UsageEvents.Event.SCREEN_INTERACTIVE)last=0;
        }
        return last;
    }

    private void scheduleFromSleep(Context c,long off,long now){
        Calendar deadline=Calendar.getInstance();deadline.setTimeInMillis(now);deadline.set(Calendar.HOUR_OF_DAY,Scheduler.wakeHour(c));deadline.set(Calendar.MINUTE,Scheduler.wakeMinute(c));deadline.set(Calendar.SECOND,0);deadline.set(Calendar.MILLISECOND,0);
        if(deadline.getTimeInMillis()<=now)deadline.add(Calendar.DAY_OF_YEAR,1);
        Calendar earliest=(Calendar)deadline.clone();earliest.set(Calendar.HOUR_OF_DAY,Scheduler.earliestHour(c));earliest.set(Calendar.MINUTE,Scheduler.earliestMinute(c));
        long wake=Math.max(earliest.getTimeInMillis(),Math.min(off+Scheduler.sleepMinutes(c)*60_000L,deadline.getTimeInMillis()));
        Calendar w=Calendar.getInstance();w.setTimeInMillis(wake);int day=w.get(Calendar.DAY_OF_WEEK);
        if(day==Calendar.SATURDAY||day==Calendar.SUNDAY){Scheduler.manager(c).cancel(Scheduler.alarmIntent(c));return;}
        if(wake>now)Scheduler.scheduleWake(c,wake);
    }
}
