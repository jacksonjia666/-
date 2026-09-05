package com.jackson.screeninsight;

import android.content.Context;import android.graphics.Canvas;import android.graphics.Color;import android.graphics.Paint;import android.view.View;

final class UsageChart extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);private long[] values;private String[] labels;private int color;
    UsageChart(Context c,long[] v,String[] l,int col){super(c);values=v;labels=l;color=col;setMinimumHeight(300);}
    @Override protected void onDraw(Canvas c){super.onDraw(c);long max=1;for(long v:values)max=Math.max(max,v);float w=getWidth()/(float)values.length,base=getHeight()-45;
        for(int i=0;i<values.length;i++){float h=(base-20)*values[i]/max;p.setColor(color);c.drawRoundRect(i*w+w*.18f,base-h,(i+1)*w-w*.18f,base,8,8,p);p.setColor(Color.LTGRAY);p.setTextSize(20);p.setTextAlign(Paint.Align.CENTER);c.drawText(labels[i],i*w+w/2,getHeight()-12,p);}}
}
