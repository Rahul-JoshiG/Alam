package com.example.alarm.services;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.example.alarm.database.Alarm;
import com.example.alarm.database.AlarmRepository;

public class RescheduleAlarmsService extends LifecycleService {
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		AlarmRepository alarmRepository = new AlarmRepository(getApplication());

		alarmRepository.getAlarmsLiveData().observe(this, alarms -> {
			for (Alarm a : alarms) {
				if (a.isStarted()) {
					a.schedule(getApplicationContext());
				}
			}
		});

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(@NonNull Intent intent) {
		super.onBind(intent);
		return null;
	}
}
