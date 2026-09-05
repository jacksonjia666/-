package com.jackson.screeninsight;

import android.app.Activity;import android.graphics.Color;import android.os.Bundle;import android.widget.Button;import android.widget.LinearLayout;import android.widget.ScrollView;import android.widget.TextView;import java.util.Calendar;import java.util.List;

public final class CategoryActivity extends Activity {
    @Override public void onCreate(Bundle b){super.onCreate(b);draw();}
    private void draw(){ScrollView sc=new ScrollView(this);LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(30,60,30,30);box.setBackgroundColor(Color.rgb(16,24,32));TextView title=new TextView(this);title.setText("App 分类\n点击 App 选择类别");title.setTextSize(22);title.setTextColor(Color.WHITE);box.addView(title);Calendar q=Calendar.getInstance();q.add(Calendar.DAY_OF_YEAR,-7);List<UsageAnalyzer.AppRow> apps=UsageAnalyzer.apps(this,q.getTimeInMillis(),System.currentTimeMillis());for(UsageAnalyzer.AppRow a:apps){Button v=new Button(this);set(v,a);v.setOnClickListener(x->choose(v,a));box.addView(v);}sc.addView(box);setContentView(sc);}
    private void choose(Button b,UsageAnalyzer.AppRow a){String[] names={"股票投资","工作学习","娱乐","微信社交","事务处理","未分类"};int[] values={2,1,-1,3,4,0};new android.app.AlertDialog.Builder(this).setTitle(a.name).setItems(names,(d,which)->{a.category=values[which];UsageAnalyzer.setCategory(this,a.pkg,a.category);set(b,a);}).show();}
    private void set(Button b,UsageAnalyzer.AppRow a){b.setText(a.name+"  ·  "+UsageAnalyzer.duration(a.ms)+"\n"+UsageAnalyzer.categoryName(a.category));}
}
