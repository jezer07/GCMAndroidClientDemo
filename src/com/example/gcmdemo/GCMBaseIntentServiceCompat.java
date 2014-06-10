package com.example.gcmdemo;

import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

abstract public class GCMBaseIntentServiceCompat extends WakefulIntentService {
	
	
	abstract protected void onMessage(Intent message);

	abstract protected void onError(Intent message);

	abstract protected void onDeleted(Intent message);

	public GCMBaseIntentServiceCompat(String name) {
		super(name);
	}
	
	

	@Override
	protected void doWakefulWork(Intent i) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(i);
		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			onError(i);
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
				.equals(messageType)) {
			onDeleted(i);
		} else {
			onMessage(i);
		}
	}
}