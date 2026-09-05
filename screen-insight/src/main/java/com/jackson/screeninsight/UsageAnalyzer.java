package com.jackson.screeninsight;

import android.app.usage.UsageEvents;import android.app.usage.UsageStats;import android.app.usage.UsageStatsManager;import android.content.Context;import android.content.pm.ApplicationInfo;import android.content.pm.PackageManager;
import java.text.SimpleDateFormat;import java.util.ArrayList;import java.util.Calendar;import java.util.Collections;import java.util.Comparator;import java.util.HashMap;import java.util.List;import java.util.Locale;import java.util.Map;

final class UsageAnalyzer {
    static final int PRODUCTIVE=1,NEUTRAL=0,LEISURE=-1;
    static final class AppRow {String pkg,name;long ms;int category;}
    static final class Report {long total,previous,late;long[] days=new long[7],hours=new long[24];long first=0,last=0,productive,leisure;List<AppRow> apps=new ArrayList<>();String advice="";}
    static Report analyze(Context c){
        Report r=new Report();Calendar end=Calendar.getInstance();long now=end.getTimeInMillis();Calendar start=(Calendar)end.clone();start.add(Calendar.DAY_OF_YEAR,-7);Calendar prev=(Calendar)start.clone();prev.add(Calendar.DAY_OF_YEAR,-7);
        r.apps=apps(c,start.getTimeInMillis(),now);for(AppRow a:r.apps){r.total+=a.ms;if(a.category==PRODUCTIVE)r.productive+=a.ms;if(a.category==LEISURE)r.leisure+=a.ms;}
        for(AppRow a:apps(c,prev.getTimeInMillis(),start.getTimeInMillis()))r.previous+=a.ms;
        sessions(c,start.getTimeInMillis(),now,r);r.advice=KnowledgeBase.advice(r);return r;
    }
    static List<AppRow> apps(Context c,long from,long to){
        UsageStatsManager us=(UsageStatsManager)c.getSystemService(Context.USAGE_STATS_SERVICE);Map<String,UsageStats> raw=us.queryAndAggregateUsageStats(from,to);List<AppRow> out=new ArrayList<>();PackageManager pm=c.getPackageManager();
        for(UsageStats u:raw.values()){if(u.getTotalTimeInForeground()<60_000||u.getPackageName().equals(c.getPackageName()))continue;AppRow a=new AppRow();a.pkg=u.getPackageName();a.ms=u.getTotalTimeInForeground();a.name=a.pkg;try{ApplicationInfo i=pm.getApplicationInfo(a.pkg,0);a.name=pm.getApplicationLabel(i).toString();}catch(Exception ignored){}a.category=category(c,a.pkg);out.add(a);}
        Collections.sort(out,(a,b)->Long.compare(b.ms,a.ms));return out;
    }
    private static void sessions(Context c,long from,long to,Report r){
        UsageStatsManager us=(UsageStatsManager)c.getSystemService(Context.USAGE_STATS_SERVICE);UsageEvents es=us.queryEvents(from,to);UsageEvents.Event e=new UsageEvents.Event();Map<String,Long> active=new HashMap<>();Calendar cal=Calendar.getInstance();
        while(es.hasNextEvent()){es.getNextEvent(e);int type=e.getEventType();String pkg=e.getPackageName();if(type==UsageEvents.Event.ACTIVITY_RESUMED)active.put(pkg,e.getTimeStamp());else if(type==UsageEvents.Event.ACTIVITY_PAUSED){Long s=active.remove(pkg);if(s==null||e.getTimeStamp()<=s)continue;long d=Math.min(e.getTimeStamp()-s,4*60*60_000L);cal.setTimeInMillis(s);int ago=(int)((to-s)/(24*60*60_000L));if(ago>=0&&ago<7)r.days[6-ago]+=d;r.hours[cal.get(Calendar.HOUR_OF_DAY)]+=d;if(cal.get(Calendar.HOUR_OF_DAY)>=23||cal.get(Calendar.HOUR_OF_DAY)<6)r.late+=d;if(r.first==0||s<r.first)r.first=s;if(e.getTimeStamp()>r.last)r.last=e.getTimeStamp();}}
    }
    static int category(Context c,String pkg){int saved=c.getSharedPreferences("categories",0).getInt(pkg,99);if(saved!=99)return saved;String p=pkg.toLowerCase(Locale.ROOT);if(p.contains("chatgpt")||p.contains("openai")||p.contains("office")||p.contains("docs")||p.contains("calendar")||p.contains("notion"))return PRODUCTIVE;if(p.contains("tiktok")||p.contains("instagram")||p.contains("facebook")||p.contains("youtube")||p.contains("reddit")||p.contains("xiaohongshu")||p.contains("xingin")||p.contains("game"))return LEISURE;return NEUTRAL;}
    static void setCategory(Context c,String pkg,int v){c.getSharedPreferences("categories",0).edit().putInt(pkg,v).apply();}
    static String duration(long ms){long m=ms/60_000;return String.format(Locale.getDefault(),"%d小时%02d分",m/60,m%60);}
    static String time(long ms){return ms==0?"暂无":new SimpleDateFormat("EEE HH:mm",Locale.getDefault()).format(ms);}
}
