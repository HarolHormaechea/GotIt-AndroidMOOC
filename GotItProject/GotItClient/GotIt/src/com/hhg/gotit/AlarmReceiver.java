package com.hhg.gotit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(getClass().getName(),"Broadcast received");
		createNotification(context);
		
	}
	
	private void createNotification(Context context){
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		Notification noti = new Notification.Builder(context)
	        .setContentTitle("Time to do a quiz!")
	        .setContentText("Click to login and fill your new quiz.")
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent)
	        .build();
		
	    NotificationManager notificationManager = 
	    		(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    // hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;
	
	    notificationManager.notify(0, noti);
	}

}
