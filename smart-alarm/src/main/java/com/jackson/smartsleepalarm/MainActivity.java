package com.jackson.smartsleepalarm;

import android.app.Activity;import android.app.AlarmManager;import android.app.AppOpsManager;import android.content.Intent;import android.graphics.Color;import android.net.Uri;import android.os.Build;import android.os.Bundle;import android.provider.Settings;import android.view.Gravity;import android.widget.Button;import android.widget.LinearLayout;import android.widget.Switch;import android.widget.TextView;

public final class MainActivity extends Activity {
    private TextView status;
    @Override public void onCreate(Bundle b){super.onCreate(b);render();}
    @Override protected void onResume(){super.onResume();if(status!=null)status.setText("下次闹铃："+Scheduler.formattedNext(this));}
    private void render(){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(42,80,42,30);box.setBackgroundColor(Color.rgb(16,18,26));
        TextView title=t("Smart Sleep Alarm",30,Color.WHITE);box.addView(title);box.addView(t("最后起床 08:15  ·  最长睡眠 8小时30分\n仅周一至周五早晨响铃",17,Color.LTGRAY));
        Switch toggle=new Switch(this);toggle.setText("每天自动运行");toggle.setTextColor(Color.WHITE);toggle.setTextSize(18);toggle.setChecked(Scheduler.enabled(this));box.addView(toggle,new LinearLayout.LayoutParams(-1,130));
        status=t("下次闹铃："+Scheduler.formattedNext(this),18,Color.rgb(157,151,255));box.addView(status);
        Button usage=new Button(this);usage.setText("① 开启使用情况访问权限");box.addView(usage,new LinearLayout.LayoutParams(-1,130));
        Button exact=new Button(this);exact.setText("② 开启闹钟和提醒权限");box.addView(exact,new LinearLayout.LayoutParams(-1,130));
        Button test=new Button(this);test.setText("测试闹铃（1分钟后）");box.addView(test,new LinearLayout.LayoutParams(-1,130));
        box.addView(t("检测时段：21:00–03:00。息屏连续15分钟后视为入睡；再次使用手机会自动重新计算。首次设置权限后无需每天打开 App。",15,Color.GRAY));setContentView(box);
        toggle.setOnCheckedChangeListener((v,on)->{getSharedPreferences(Scheduler.PREFS,0).edit().putBoolean(Scheduler.ENABLED,on).apply();if(on)Scheduler.scheduleMonitor(this,System.currentTimeMillis()+3000);else Scheduler.cancelAll(this);status.setText(on?"已启动，等待检测":"自动闹铃已关闭");});
        usage.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));
        exact.setOnClickListener(v->{if(Build.VERSION.SDK_INT>=31)startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:"+getPackageName())));});
        test.setOnClickListener(v->{Scheduler.scheduleWake(this,System.currentTimeMillis()+60_000);status.setText("测试闹铃已设为1分钟后");});
        if(Build.VERSION.SDK_INT>=33)requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},50);
        if(Scheduler.enabled(this))Scheduler.scheduleMonitor(this,System.currentTimeMillis()+3000);
    }
    private TextView t(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.CENTER_VERTICAL);v.setPadding(0,14,0,14);return v;}
}
