package com.jackson.queenslandsnake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

final class SnakeView extends View {
    private static final int COLS = 18, ROWS = 26, TICK_MS = 135;
    private static final String[] NAMES = {
            "Eastern brown", "Coastal taipan", "Common death adder",
            "Red-bellied black", "Tiger snake"
    };
    private static final String[] SCI = {
            "Pseudonaja textilis", "Oxyuranus scutellatus", "Acanthophis antarcticus",
            "Pseudechis porphyriacus", "Notechis scutatus"
    };
    private static final int[] BODY = {
            Color.rgb(151,105,63), Color.rgb(176,104,54), Color.rgb(151,119,74),
            Color.rgb(35,39,38), Color.rgb(126,105,57)
    };
    private static final int[] MARK = {
            Color.rgb(94,62,39), Color.rgb(80,49,30), Color.rgb(61,44,31),
            Color.rgb(202,47,39), Color.rgb(32,31,26)
    };

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Handler clock = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final ArrayList<Point> snake = new ArrayList<>();
    private int screen = 0, selected = 0, dx = 1, dy = 0, nextDx = 1, nextDy = 0;
    private int foodX, foodY, score, best;
    private float downX, downY, cell, left, top;
    private final Runnable tick = () -> { if (screen == 1) step(); };

    SnakeView(Context context) { super(context); p.setTypeface(android.graphics.Typeface.create("sans", 0)); }

    @Override protected void onDraw(Canvas c) {
        c.drawColor(Color.rgb(14, 30, 23));
        if (screen == 0) drawPicker(c); else drawGame(c);
    }

    private void drawPicker(Canvas c) {
        text(c, "QUEENSLAND SNAKE", getWidth()/2f, 70, 26, Color.rgb(233,196,106), true);
        text(c, "Choose your snake", getWidth()/2f, 104, 15, Color.LTGRAY, true);
        float cardH = Math.min(92, (getHeight()-190)/5f), y = 130;
        for (int i=0;i<5;i++,y+=cardH+8) {
            p.setColor(i==selected ? Color.rgb(42, 74, 57) : Color.rgb(25,48,37));
            c.drawRoundRect(new RectF(22,y,getWidth()-22,y+cardH),14,14,p);
            drawSample(c, i, 48, y+cardH/2, getWidth()*.30f);
            text(c,NAMES[i],getWidth()*.42f,y+cardH*.43f,16,Color.WHITE,false);
            text(c,SCI[i],getWidth()*.42f,y+cardH*.70f,11,Color.rgb(171,190,179),false);
        }
        p.setColor(Color.rgb(233,196,106));
        c.drawRoundRect(new RectF(22,getHeight()-66,getWidth()-22,getHeight()-18),14,14,p);
        text(c,"PLAY",getWidth()/2f,getHeight()-35,18,Color.rgb(14,30,23),true);
    }

    private void drawSample(Canvas c,int kind,float x,float y,float length) {
        p.setStrokeCap(Paint.Cap.ROUND); p.setStrokeWidth(18); p.setColor(BODY[kind]);
        c.drawLine(x,y,x+length,y,p);
        p.setStrokeWidth(5); p.setColor(MARK[kind]);
        for(float q=x+18;q<x+length;q+=22) c.drawPoint(q,y,p);
    }

    private void drawGame(Canvas c) {
        text(c,"SCORE  "+score,20,42,18,Color.WHITE,false);
        text(c,"BEST  "+best,getWidth()-20,42,18,Color.rgb(233,196,106),true);
        cell = Math.min(getWidth()/(float)COLS,(getHeight()-110)/(float)ROWS);
        left=(getWidth()-cell*COLS)/2; top=64;
        p.setColor(Color.rgb(20,42,31));
        c.drawRect(left,top,left+cell*COLS,top+cell*ROWS,p);
        p.setColor(Color.rgb(235,87,70));
        c.drawCircle(left+(foodX+.5f)*cell,top+(foodY+.5f)*cell,cell*.34f,p);
        for(int i=snake.size()-1;i>=0;i--) {
            Point q=snake.get(i); p.setColor(i==0?MARK[selected]:BODY[selected]);
            float pad=i==0?cell*.08f:cell*.13f;
            c.drawRoundRect(new RectF(left+q.x*cell+pad,top+q.y*cell+pad,
                    left+(q.x+1)*cell-pad,top+(q.y+1)*cell-pad),cell*.2f,cell*.2f,p);
            if(i>0 && (selected==2 || selected==4) && i%2==0){
                p.setColor(MARK[selected]);
                c.drawRect(left+q.x*cell+cell*.2f,top+q.y*cell+cell*.2f,
                        left+(q.x+1)*cell-cell*.2f,top+(q.y+1)*cell-cell*.2f,p);
            }
        }
        text(c,"Swipe to steer  •  Tap top-left to change snake",getWidth()/2f,getHeight()-22,11,Color.GRAY,true);
    }

    private void startGame() {
        screen=1; score=0; dx=nextDx=1; dy=nextDy=0; snake.clear();
        snake.add(new Point(7,13)); snake.add(new Point(6,13)); snake.add(new Point(5,13));
        placeFood(); clock.removeCallbacks(tick); clock.postDelayed(tick,TICK_MS); invalidate();
    }

    private void step() {
        dx=nextDx; dy=nextDy; Point h=snake.get(0); Point n=new Point(h.x+dx,h.y+dy);
        if(n.x<0||n.x>=COLS||n.y<0||n.y>=ROWS||contains(n.x,n.y)) { best=Math.max(best,score); startGame(); return; }
        snake.add(0,n);
        if(n.x==foodX&&n.y==foodY){ score++; placeFood(); } else snake.remove(snake.size()-1);
        invalidate(); clock.postDelayed(tick,Math.max(72,TICK_MS-score*2));
    }

    private boolean contains(int x,int y){ for(Point q:snake) if(q.x==x&&q.y==y)return true; return false; }
    private void placeFood(){ do { foodX=random.nextInt(COLS); foodY=random.nextInt(ROWS); } while(contains(foodX,foodY)); }

    @Override public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();return true;}
        if(e.getAction()!=MotionEvent.ACTION_UP)return true;
        float x=e.getX(),y=e.getY();
        if(screen==0){
            float cardH=Math.min(92,(getHeight()-190)/5f), start=130;
            if(y>=start&&y<start+5*(cardH+8)){int choice=(int)((y-start)/(cardH+8)); if(choice<5)selected=choice;}
            if(y>getHeight()-82)startGame(); else invalidate(); return true;
        }
        if(downX<90&&downY<64){clock.removeCallbacks(tick);screen=0;invalidate();return true;}
        float sx=x-downX, sy=y-downY; if(Math.abs(sx)<18&&Math.abs(sy)<18)return true;
        int ndx=0,ndy=0; if(Math.abs(sx)>Math.abs(sy))ndx=sx>0?1:-1; else ndy=sy>0?1:-1;
        if(ndx!=-dx||ndy!=-dy){nextDx=ndx;nextDy=ndy;} return true;
    }

    private void text(Canvas c,String s,float x,float y,float size,int color,boolean centered){
        p.setTextSize(size);p.setColor(color);p.setTextAlign(centered?Paint.Align.CENTER:Paint.Align.LEFT);c.drawText(s,x,y,p);
    }

    @Override protected void onDetachedFromWindow(){clock.removeCallbacks(tick);super.onDetachedFromWindow();}
}
