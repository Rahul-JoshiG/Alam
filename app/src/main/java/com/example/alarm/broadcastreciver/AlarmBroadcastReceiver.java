package com.example.alarm.broadcastreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.alarm.R;
import com.example.alarm.database.Alarm;

import java.util.Calendar;

import com.example.alarm.services.AlarmService;
import com.example.alarm.services.RescheduleAlarmsService;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
	private Alarm alarm;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			String toastText = context.getString(R.string.toast_alarm_reboot);
			Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
			startRescheduleAlarmsService(context);

		}
		else
		{
			Bundle bundle = intent.getBundleExtra(context.getString(R.string.bundle_alarm_obj));
			if (bundle != null)
				alarm = (Alarm) bundle.getSerializable(context.getString(R.string.arg_alarm_obj));
			String toastText = context.getString(R.string.toast_alarm_received);
			Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
			if (alarm != null && (!alarm.isRecurring() || isAlarmToday(alarm))) {
				startAlarmService(context, alarm);
			}
		}
	}

	private boolean isAlarmToday(Alarm alarm) {
		Calendar calendar = Calendar.getInstance();
		int today = calendar.get(Calendar.DAY_OF_WEEK);

		switch (today) {
			case Calendar.MONDAY:
				return alarm.isMonday();
			case Calendar.TUESDAY:
				return alarm.isTuesday();
			case Calendar.WEDNESDAY:
				return alarm.isWednesday();
			case Calendar.THURSDAY:
				return alarm.isThursday();
			case Calendar.FRIDAY:
				return alarm.isFriday();
			case Calendar.SATURDAY:
				return alarm.isSaturday();
			case Calendar.SUNDAY:
				return alarm.isSunday();
			default:
				return false;
		}
	}

	private void startAlarmService(Context context, Alarm alarm) {
		Intent intentService = new Intent(context, AlarmService.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(context.getString(R.string.arg_alarm_obj), alarm);
		intentService.putExtra(context.getString(R.string.bundle_alarm_obj), bundle);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(intentService);
		} else {
			context.startService(intentService);
		}
	}

	private void startRescheduleAlarmsService(Context context) {
		Intent intentService = new Intent(context, RescheduleAlarmsService.class);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(intentService);
		} else {
			context.startService(intentService);
		}
	}
}
