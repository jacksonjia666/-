package com.jackson.screeninsight;

final class KnowledgeBase {
    static String advice(UsageAnalyzer.Report r){
        StringBuilder s=new StringBuilder();double change=r.previous==0?0:(r.total-r.previous)*100.0/r.previous;
        if(r.previous>0)s.append(change>10?"本周使用明显增加，建议先检查增长最多的娱乐 App。":change<-10?"本周屏幕时间下降，节奏改善得不错。":"本周总量与上周接近。");
        if(r.late>3*60*60_000L)s.append(" 深夜23点后的使用偏多，睡前30分钟可以改成无屏活动。");
        if(r.leisure>r.productive*2&&r.leisure>7*60*60_000L)s.append(" 娱乐时间显著高于工作学习时间，可先给使用最多的娱乐 App 设置每日上限。");
        else if(r.productive>r.leisure)s.append(" 工作学习类占比高，但仍建议每连续使用一小时离屏休息。");
        if(s.length()==0)s.append("数据还不够，继续使用一周后会给出趋势建议。");return s.toString();
    }
}
