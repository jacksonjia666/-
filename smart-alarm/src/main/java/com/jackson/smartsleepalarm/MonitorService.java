package com.jackson.smartsleepalarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public final class MonitorService extends Service {
    private static final int NOTIFICATION_ID=200;
    private final BroadcastReceiver screenReceiver=new BroadcastReceiver(){
        @Override public void onReceive(Context c,Intent i){
            if(Intent.ACTION_SCREEN_OFF.equals(i.getAction())){
                Scheduler.scheduleMonitor(c,System.currentTimeMillis()+15*60_000L);
            }else if(Intent.ACTION_SCREEN_ON.equals(i.getAction())){
                Scheduler.manager(c).cancel(Scheduler.alarmIntent(c));
                c.getSharedPreferences(Scheduler.PREFS,0).edit().remove(Scheduler.NEXT).apply();
                Scheduler.scheduleMonitor(c,System.currentTimeMillis()+10*60_000L);
            }
        }
    };

    @Override public void onCreate(){super.onCreate();
        String channel="smart_alarm_monitor";NotificationManager nm=getSystemService(NotificationManager.class);
        NotificationChannel nc=new NotificationChannel(channel,"智能闹铃后台运行",NotificationManager.IMPORTANCE_LOW);nc.setSound(null,null);nm.createNotificationChannel(nc);
        Intent open=new Intent(this,MainActivity.class);PendingIntent pi=PendingIntent.getActivity(this,21,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification n=new Notification.Builder(this,channel).setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("智能闹铃正在运行").setContentText("正在等待夜间最后一次息屏").setOngoing(true).setContentIntent(pi).build();
        startForeground(NOTIFICATION_ID,n);
        IntentFilter f=new IntentFilter();f.addAction(Intent.ACTION_SCREEN_OFF);f.addAction(Intent.ACTION_SCREEN_ON);registerReceiver(screenReceiver,f);
        Scheduler.scheduleMonitor(this,System.currentTimeMillis()+3000);
    }
    @Override public int onStartCommand(Intent i,int flags,int id){if(!Scheduler.enabled(this)){stopSelf();return START_NOT_STICKY;}return START_STICKY;}
    @Override public void onTaskRemoved(Intent rootIntent){if(Scheduler.enabled(this))Scheduler.scheduleMonitor(this,System.currentTimeMillis()+3000);super.onTaskRemoved(rootIntent);}
    @Override public void onDestroy(){try{unregisterReceiver(screenReceiver);}catch(Exception ignored){}super.onDestroy();}
    @Override public IBinder onBind(Intent i){return null;}
}
