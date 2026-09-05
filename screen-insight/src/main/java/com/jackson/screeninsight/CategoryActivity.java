package com.jackson.screeninsight;

import android.app.Activity;import android.graphics.Color;import android.os.Bundle;import android.widget.Button;import android.widget.LinearLayout;import android.widget.ScrollView;import android.widget.TextView;import java.util.Calendar;import java.util.List;

public final class CategoryActivity extends Activity {
    @Override public void onCreate(Bundle b){super.onCreate(b);draw();}
    private void draw(){ScrollView sc=new ScrollView(this);LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(30,60,30,30);box.setBackgroundColor(Color.rgb(16,24,32));TextView title=new TextView(this);title.setText("App 分类\n点击可切换：未分类 → 工作/学习 → 娱乐");title.setTextSize(22);title.setTextColor(Color.WHITE);box.addView(title);Calendar q=Calendar.getInstance();q.add(Calendar.DAY_OF_YEAR,-7);List<UsageAnalyzer.AppRow> apps=UsageAnalyzer.apps(this,q.getTimeInMillis(),System.currentTimeMillis());for(UsageAnalyzer.AppRow a:apps){Button v=new Button(this);set(v,a);v.setOnClickListener(x->{a.category=a.category==0?1:a.category==1?-1:0;UsageAnalyzer.setCategory(this,a.pkg,a.category);set(v,a);});box.addView(v);}sc.addView(box);setContentView(sc);}
    private void set(Button b,UsageAnalyzer.AppRow a){String c=a.category==1?"工作/学习":a.category==-1?"娱乐":"未分类";b.setText(a.name+"  ·  "+UsageAnalyzer.duration(a.ms)+"\n"+c);}
}
