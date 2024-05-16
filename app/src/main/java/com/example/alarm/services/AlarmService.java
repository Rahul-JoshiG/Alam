package com.example.alarm.services;

import static com.example.alarm.App.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.alarm.Activities.RingActivity;
import com.example.alarm.R;
import com.example.alarm.database.Alarm;

import java.io.IOException;

public class AlarmService extends Service {
	private MediaPlayer mediaPlayer;
	private Vibrator vibrator;
	Alarm alarm;
	Uri ringtone;
	@Override
	public void onCreate() {
		super.onCreate();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setLooping(true);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = intent.getBundleExtra(getString(R.string.bundle_alarm_obj));
		if (bundle != null) {
			alarm = (Alarm) bundle.getSerializable(getString(R.string.arg_alarm_obj));
		}

		// Setting up notification intent
		Intent notificationIntent = new Intent(this, RingActivity.class);
		notificationIntent.putExtra(getString(R.string.bundle_alarm_obj), bundle);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Setting data source for media player
		Uri alarmUri = (alarm != null) ? Uri.parse(alarm.getTone()) : ringtone;
		try {
			mediaPlayer.setDataSource(this.getBaseContext(), alarmUri);
			mediaPlayer.prepareAsync();
		} catch (IOException ex) {
			//noinspection CallToPrintStackTrace
			ex.printStackTrace();
		}

		// Notification setup
		Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setContentTitle("Ring Ring .. Ring Ring")
				.setContentText(alarm.getTitle())
				.setSmallIcon(R.drawable.baseline_access_alarm_24)
				// Set sound for notification
				.setSound(alarmUri)
				.setCategory(NotificationCompat.CATEGORY_ALARM)
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
				.setPriority(NotificationCompat.PRIORITY_MAX)
				.setFullScreenIntent(pendingIntent, true)
				.build();

		// Media player prepared listener
		mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());

		// Vibration setup
		if (alarm != null && alarm.isVibrate()) {
			long[] pattern = {0, 100, 1000};
			vibrator.vibrate(pattern, 0);
		}

		// Start service in foreground with notification
		startForeground(1, notification);

		return START_STICKY;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();

		mediaPlayer.stop();
		vibrator.cancel();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}