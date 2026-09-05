package com.jackson.smartsleepalarm;

import android.app.Activity;import android.app.TimePickerDialog;import android.content.Intent;import android.graphics.Color;import android.net.Uri;import android.os.Build;import android.os.Bundle;import android.provider.Settings;import android.view.Gravity;import android.widget.Button;import android.widget.LinearLayout;import android.widget.NumberPicker;import android.widget.Switch;import android.widget.TextView;import android.app.AlertDialog;

public final class MainActivity extends Activity {
    private TextView status;
    private Button earliestButton, wakeButton, sleepButton;
    @Override public void onCreate(Bundle b){super.onCreate(b);render();}
    @Override protected void onResume(){super.onResume();if(status!=null)status.setText("下次闹铃："+Scheduler.formattedNext(this));}
    private void render(){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(42,80,42,30);box.setBackgroundColor(Color.rgb(16,18,26));
        TextView title=t("Smart Sleep Alarm",30,Color.WHITE);box.addView(title);box.addView(t("根据最后息屏时间自动计算\n仅周一至周五早晨响铃",17,Color.LTGRAY));
        Switch toggle=new Switch(this);toggle.setText("每天自动运行");toggle.setTextColor(Color.WHITE);toggle.setTextSize(18);toggle.setChecked(Scheduler.enabled(this));box.addView(toggle,new LinearLayout.LayoutParams(-1,130));
        status=t("下次闹铃："+Scheduler.formattedNext(this),18,Color.rgb(157,151,255));box.addView(status);
        earliestButton=new Button(this);earliestButton.setText(earliestText());box.addView(earliestButton,new LinearLayout.LayoutParams(-1,130));
        wakeButton=new Button(this);wakeButton.setText(wakeText());box.addView(wakeButton,new LinearLayout.LayoutParams(-1,130));
        sleepButton=new Button(this);sleepButton.setText(sleepText());box.addView(sleepButton,new LinearLayout.LayoutParams(-1,130));
        Button usage=new Button(this);usage.setText("① 开启使用情况访问权限");box.addView(usage,new LinearLayout.LayoutParams(-1,130));
        Button exact=new Button(this);exact.setText("② 开启闹钟和提醒权限");box.addView(exact,new LinearLayout.LayoutParams(-1,130));
        Button test=new Button(this);test.setText("测试闹铃（1分钟后）");box.addView(test,new LinearLayout.LayoutParams(-1,130));
        box.addView(t("检测时段：21:00–03:00。息屏连续15分钟后视为入睡；再次使用手机会自动重新计算。首次设置权限后无需每天打开 App。",15,Color.GRAY));setContentView(box);
        toggle.setOnCheckedChangeListener((v,on)->{getSharedPreferences(Scheduler.PREFS,0).edit().putBoolean(Scheduler.ENABLED,on).apply();if(on)Scheduler.scheduleMonitor(this,System.currentTimeMillis()+3000);else Scheduler.cancelAll(this);status.setText(on?"已启动，等待检测":"自动闹铃已关闭");});
        earliestButton.setOnClickListener(v->new TimePickerDialog(this,(picker,hour,minute)->{
            int latest=Scheduler.wakeHour(this)*60+Scheduler.wakeMinute(this);
            if(hour*60+minute>latest){status.setText("最早时间不能晚于最晚起床时间");return;}
            getSharedPreferences(Scheduler.PREFS,0).edit().putInt(Scheduler.EARLIEST_HOUR,hour).putInt(Scheduler.EARLIEST_MINUTE,minute).apply();
            earliestButton.setText(earliestText());recalculate();
        },Scheduler.earliestHour(this),Scheduler.earliestMinute(this),true).show());
        wakeButton.setOnClickListener(v->new TimePickerDialog(this,(picker,hour,minute)->{
            int earliest=Scheduler.earliestHour(this)*60+Scheduler.earliestMinute(this);
            if(hour*60+minute<earliest){status.setText("最晚时间不能早于最早起床时间");return;}
            getSharedPreferences(Scheduler.PREFS,0).edit().putInt(Scheduler.WAKE_HOUR,hour).putInt(Scheduler.WAKE_MINUTE,minute).apply();
            wakeButton.setText(wakeText());recalculate();
        },Scheduler.wakeHour(this),Scheduler.wakeMinute(this),true).show());
        sleepButton.setOnClickListener(v->showSleepPicker());
        usage.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));
        exact.setOnClickListener(v->{if(Build.VERSION.SDK_INT>=31)startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:"+getPackageName())));});
        test.setOnClickListener(v->{Scheduler.scheduleWake(this,System.currentTimeMillis()+60_000);status.setText("测试闹铃已设为1分钟后");});
        if(Build.VERSION.SDK_INT>=33)requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},50);
        if(Scheduler.enabled(this))Scheduler.scheduleMonitor(this,System.currentTimeMillis()+3000);
    }
    private String earliestText(){return String.format(java.util.Locale.getDefault(),"最早起床时间：%02d:%02d",Scheduler.earliestHour(this),Scheduler.earliestMinute(this));}
    private String wakeText(){return String.format(java.util.Locale.getDefault(),"最晚起床时间：%02d:%02d",Scheduler.wakeHour(this),Scheduler.wakeMinute(this));}
    private String sleepText(){int m=Scheduler.sleepMinutes(this);return "最长睡眠时间："+(m/60)+"小时"+(m%60)+"分钟";}
    private void showSleepPicker(){
        LinearLayout row=new LinearLayout(this);row.setPadding(35,10,35,10);row.setGravity(Gravity.CENTER);
        NumberPicker hours=new NumberPicker(this);hours.setMinValue(4);hours.setMaxValue(12);hours.setValue(Scheduler.sleepMinutes(this)/60);
        NumberPicker minutes=new NumberPicker(this);minutes.setMinValue(0);minutes.setMaxValue(3);minutes.setDisplayedValues(new String[]{"00分钟","15分钟","30分钟","45分钟"});minutes.setValue((Scheduler.sleepMinutes(this)%60)/15);
        row.addView(hours,new LinearLayout.LayoutParams(0,-2,1));row.addView(minutes,new LinearLayout.LayoutParams(0,-2,1));
        new AlertDialog.Builder(this).setTitle("设置最长睡眠时间").setView(row).setNegativeButton("取消",null).setPositiveButton("保存",(d,w)->{
            int total=hours.getValue()*60+minutes.getValue()*15;getSharedPreferences(Scheduler.PREFS,0).edit().putInt(Scheduler.SLEEP_MINUTES,total).apply();sleepButton.setText(sleepText());recalculate();
        }).show();
    }
    private void recalculate(){
        Scheduler.manager(this).cancel(Scheduler.alarmIntent(this));getSharedPreferences(Scheduler.PREFS,0).edit().remove(Scheduler.NEXT).apply();
        if(Scheduler.enabled(this))Scheduler.scheduleMonitor(this,System.currentTimeMillis()+1000);status.setText("设置已保存，正在按新规则重新计算");
    }
    private TextView t(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.CENTER_VERTICAL);v.setPadding(0,14,0,14);return v;}
}
