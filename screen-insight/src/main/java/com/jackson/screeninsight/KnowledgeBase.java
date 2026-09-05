package com.jackson.screeninsight;

final class KnowledgeBase {
    static String advice(UsageAnalyzer.Report r){
        double change=r.previous==0?0:(r.total-r.previous)*100.0/r.previous,lateChange=r.previousLate==0?0:(r.late-r.previousLate)*100.0/r.previousLate;String good,watch,next;
        if(r.previous==0)good="数据正在建立基线，分类完成后判断会更准确。";else if(change<-8)good="总屏幕时间比上周少了"+Math.round(-change)+"%，控制得不错。";else if(lateChange<-10)good="深夜使用比上周下降了"+Math.round(-lateChange)+"%，作息方向在改善。";else if(r.productive>r.leisure)good="工作学习类时间高于娱乐类，使用目的比较清晰。";else good="本周使用节奏总体稳定，没有明显失控。";
        if(r.late>3*60*60_000L){String cause=r.lateLeisure>r.lateProductive?"娱乐":"工作学习";watch="23点后累计使用"+UsageAnalyzer.duration(r.late)+"，主要来自"+cause+(r.topLateApp.isEmpty()?"":"，其中“"+r.topLateApp+"”最突出")+"。";}else if(change>12)watch="总使用时间比上周增加"+Math.round(change)+"%，值得看看增长是否来自真正重要的事情。";else watch="暂时没有明显风险点，继续观察最后使用时间是否稳定。";
        next=r.lateLeisure>60*60_000L?"下一步：先把深夜娱乐减少30分钟，观察一周后白天精神是否有变化。":r.lateProductive>60*60_000L?"下一步：最近深夜仍在工作或学习，别只看效率，也留意第二天精力；可以尝试提前30分钟收尾。":"下一步：保持当前节奏，并留意睡眠、精神和专注力是否同步改善。";
        return "做得好："+good+"\n需要留意："+watch+"\n"+next;
    }
}
