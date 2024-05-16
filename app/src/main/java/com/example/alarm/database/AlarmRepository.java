package com.example.alarm.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alarm.database.Alarm;
import com.example.alarm.database.AlarmDao;
import com.example.alarm.database.AlarmDatabase;

import java.util.List;

public class AlarmRepository {
	private final AlarmDao alarmDao;
	private final LiveData<List<Alarm>> alarmsLiveData;

	public AlarmRepository(Application application) {
		AlarmDatabase db = AlarmDatabase.getDatabase(application);
		alarmDao = db.alarmDao();
		alarmsLiveData = alarmDao.getAlarms();
	}

	public void insert(Alarm alarm) {
		AlarmDatabase.databaseWriteExecutor.execute(() -> {
			alarmDao.insert(alarm);
		});
	}

	public void update(Alarm alarm) {
		AlarmDatabase.databaseWriteExecutor.execute(() -> {
			alarmDao.update(alarm);
		});
	}

	public LiveData<List<Alarm>> getAlarmsLiveData() {
		return alarmsLiveData;
	}

	public void delete(int alarmId){
		AlarmDatabase.databaseWriteExecutor.execute(() -> {
			alarmDao.delete(alarmId);
		});
	}
}