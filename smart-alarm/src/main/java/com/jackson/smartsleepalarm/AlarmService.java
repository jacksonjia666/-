package com.jackson.smartsleepalarm;

import android.app.Notification;import android.app.NotificationChannel;import android.app.NotificationManager;import android.app.PendingIntent;import android.app.Service;
import android.content.Intent;import android.media.AudioAttributes;import android.media.Ringtone;import android.media.RingtoneManager;import android.os.IBinder;import android.os.Vibrator;import android.os.VibrationEffect;

public final class AlarmService extends Service {
    private Ringtone ringtone;private Vibrator vibrator;
    @Override public void onCreate(){super.onCreate();
        NotificationManager nm=getSystemService(NotificationManager.class);String id="smart_alarm_ring";
        nm.createNotificationChannel(new NotificationChannel(id,"Smart alarm",NotificationManager.IMPORTANCE_HIGH));
        Intent open=new Intent(this,AlarmActivity.class);open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi=PendingIntent.getActivity(this,20,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification n=new Notification.Builder(this,id).setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("起床时间到了").setContentText("点击停止闹铃").setCategory(Notification.CATEGORY_ALARM).setOngoing(true).setFullScreenIntent(pi,true).setContentIntent(pi).build();
        startForeground(100,n);ringtone=RingtoneManager.getRingtone(this,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        if(ringtone!=null){ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());ringtone.setLooping(true);ringtone.play();}
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,800,400},0));startActivity(open);
    }
    @Override public int onStartCommand(Intent i,int f,int id){return START_NOT_STICKY;}
    @Override public void onDestroy(){if(ringtone!=null)ringtone.stop();if(vibrator!=null)vibrator.cancel();stopForeground(STOP_FOREGROUND_REMOVE);super.onDestroy();}
    @Override public IBinder onBind(Intent i){return null;}
}
