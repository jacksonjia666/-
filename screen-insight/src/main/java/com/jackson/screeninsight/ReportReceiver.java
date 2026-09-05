package com.jackson.screeninsight;

import android.app.Notification;import android.app.NotificationChannel;import android.app.NotificationManager;import android.app.PendingIntent;import android.content.BroadcastReceiver;import android.content.Context;import android.content.Intent;

public final class ReportReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent i){UsageAnalyzer.Report r=UsageAnalyzer.analyze(c);NotificationManager nm=c.getSystemService(NotificationManager.class);String id="weekly_screen_report";nm.createNotificationChannel(new NotificationChannel(id,"每周屏幕时间报告",NotificationManager.IMPORTANCE_DEFAULT));PendingIntent open=PendingIntent.getActivity(c,91,new Intent(c,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);String title="本周屏幕时间 "+UsageAnalyzer.duration(r.total);String body=r.advice;Notification n=new Notification.Builder(c,id).setSmallIcon(android.R.drawable.ic_menu_recent_history).setContentTitle(title).setContentText(body).setStyle(new Notification.BigTextStyle().bigText(body)).setContentIntent(open).setAutoCancel(true).build();nm.notify(300,n);ReportScheduler.schedule(c);}
}
