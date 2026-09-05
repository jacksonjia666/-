package com.jackson.screeninsight;
import android.content.BroadcastReceiver;import android.content.Context;import android.content.Intent;
public final class BootReceiver extends BroadcastReceiver {@Override public void onReceive(Context c,Intent i){ReportScheduler.schedule(c);ReportScheduler.scheduleDaily(c);}}
