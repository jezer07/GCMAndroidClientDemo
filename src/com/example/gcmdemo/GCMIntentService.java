package com.example.gcmdemo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class GCMIntentService extends GCMBaseIntentServiceCompat {
	public GCMIntentService() {
		super("GCMIntentService");
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onMessage(Intent message) {
		dumpEvent("onMessage", message);
		
		Bundle extras = message.getExtras();
		Intent resultIntent = new Intent(this, MessageActivity.class);
		resultIntent.putExtras(extras);
		
		 PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		
		

	
		 Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
			Notification n = new Notification.Builder(this)
			.setContentTitle("You have a new message")
			.setContentText("From: "+ extras.getString("sender"))
			.setSound(soundUri)
			.setContentIntent(resultPendingIntent)
			.setOnlyAlertOnce(true).setSmallIcon(R.drawable.ic_launcher)
			.setAutoCancel(true).build();
			
			n.defaults = Notification.DEFAULT_ALL;

	NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

	notificationManager.notify(0, n);
		
	
		
	}

	@Override
	protected void onError(Intent message) {
		dumpEvent("onError", message);
	}

	@Override
	protected void onDeleted(Intent message) {
		dumpEvent("onDeleted", message);
	}

	private void dumpEvent(String event, Intent message) {
		Bundle extras = message.getExtras();
		for (String key : extras.keySet()) {
			Log.d(getClass().getSimpleName(),
					String.format("%s: %s=%s", event, key,
							extras.getString(key)));
		}
	}
}