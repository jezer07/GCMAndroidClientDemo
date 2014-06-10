package com.example.gcmdemo;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GCMBroadcastReceiverCompat extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    intent.setClass(context, GCMIntentService.class);
    WakefulIntentService.sendWakefulWork(context, intent);
    
    
  }
  
}