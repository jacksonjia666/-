package com.jackson.smartsleepalarm;

import android.app.Activity;import android.graphics.Color;import android.os.Bundle;import android.view.Gravity;import android.view.WindowManager;import android.widget.Button;import android.widget.LinearLayout;import android.widget.TextView;

public final class AlarmActivity extends Activity {
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);box.setPadding(36,36,36,36);box.setBackgroundColor(Color.rgb(16,18,26));
        TextView title=new TextView(this);title.setText("智能起床闹铃");title.setTextSize(28);title.setTextColor(Color.WHITE);title.setGravity(Gravity.CENTER);box.addView(title);
        Button stop=new Button(this);stop.setText("停止闹铃");LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,150);lp.setMargins(0,60,0,0);box.addView(stop,lp);
        stop.setOnClickListener(v->{stopService(new android.content.Intent(this,AlarmService.class));getSharedPreferences(Scheduler.PREFS,0).edit().remove(Scheduler.NEXT).apply();Scheduler.scheduleNextWindow(this);finish();});setContentView(box);
    }
}
