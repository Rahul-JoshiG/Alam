package com.example.alarm.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.alarm.database.Alarm;
import com.example.alarm.database.AlarmRepository;

public class CreateAlarmViewModel extends AndroidViewModel {
	private AlarmRepository alarmRepository;

	public CreateAlarmViewModel(@NonNull Application application) {
		super(application);

		alarmRepository = new AlarmRepository(application);
	}

	public void insert(Alarm alarm) {
		alarmRepository.insert(alarm);
	}
	public void update(Alarm alarm) {
		alarmRepository.update(alarm);
	}
}