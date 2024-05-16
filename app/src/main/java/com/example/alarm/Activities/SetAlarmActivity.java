package com.example.alarm.Activities;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.alarm.Activities.MainActivity;
import com.example.alarm.R;
import com.example.alarm.database.Alarm;
import com.example.alarm.databinding.ActivitySetAlarmBinding;
import com.example.alarm.util.DayUtil;
import com.example.alarm.util.TimePickerUtil;
import com.example.alarm.viewmodel.CreateAlarmViewModel;

import java.util.Random;

public class SetAlarmActivity extends AppCompatActivity {

	private CreateAlarmViewModel createAlarmViewModel;
	private ActivitySetAlarmBinding setAlarmBinding;
	private String tone;
	private Ringtone ringtone;
	private Alarm alarm;

	private boolean isVibrate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EdgeToEdge.enable(this);
		setContentView(R.layout.activity_set_alarm);
		setAlarmBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_alarm);

		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
			Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
			return insets;
		});

		createAlarmViewModel = new ViewModelProvider(this).get(CreateAlarmViewModel.class);


		tone = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM).toString();
		ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(tone));

		setAlarmBinding.fragmentCreatealarmSetToneName.setText(ringtone.getTitle(this));
		if (alarm != null) {
			updateAlarmInfo(alarm);
		}

		setAlarmBinding.fragmentCreatealarmRecurring.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				setAlarmBinding.fragmentCreatealarmRecurringOptions.setVisibility(View.VISIBLE);
			} else {
				setAlarmBinding.fragmentCreatealarmRecurringOptions.setVisibility(View.GONE);
			}
		});

		setAlarmBinding.fragmentCreatealarmScheduleAlarm.setOnClickListener(v -> {
			if (alarm != null) {
				updateAlarm();
			} else {
				scheduleAlarm();
			}
		});

		setAlarmBinding.fragmentCreatealarmCardSound.setOnClickListener(view -> {
			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound");
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(tone));
			startActivityForResult(intent, 5);
		});

		setAlarmBinding.fragmentCreatealarmVibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				isVibrate = b;
			}
		});

		setAlarmBinding.fragmentCreatealarmTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker timePicker, int i, int i1) {
				setAlarmBinding.alarmHeading.setText(DayUtil.getDay(TimePickerUtil.getTimePickerHour(timePicker), TimePickerUtil.getTimePickerMinute(timePicker)));
			}
		});
	}

	private void scheduleAlarm() {
		String alarmTitle = getString(R.string.alarm_title);
		int alarmId = new Random().nextInt(Integer.MAX_VALUE);
		if (!setAlarmBinding.fragmentCreatealarmTitle.getText().toString().isEmpty()) {
			alarmTitle = setAlarmBinding.fragmentCreatealarmTitle.getText().toString();
		}
		Alarm alarm = new Alarm(
				alarmId,
				TimePickerUtil.getTimePickerHour(setAlarmBinding.fragmentCreatealarmTimePicker),
				TimePickerUtil.getTimePickerMinute(setAlarmBinding.fragmentCreatealarmTimePicker),
				alarmTitle,
				true,
				setAlarmBinding.fragmentCreatealarmRecurring.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckMon.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckTue.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckWed.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckThu.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckFri.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckSat.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckSun.isChecked(),
				tone,
				isVibrate
		);

		createAlarmViewModel.insert(alarm);

		alarm.schedule(this);
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private void updateAlarm() {
		String alarmTitle = getString(R.string.alarm_title);
		if (!setAlarmBinding.fragmentCreatealarmTitle.getText().toString().isEmpty()) {
			alarmTitle = setAlarmBinding.fragmentCreatealarmTitle.getText().toString();
		}
		Alarm updatedAlarm = new Alarm(
				alarm.getAlarmId(),
				TimePickerUtil.getTimePickerHour(setAlarmBinding.fragmentCreatealarmTimePicker),
				TimePickerUtil.getTimePickerMinute(setAlarmBinding.fragmentCreatealarmTimePicker),
				alarmTitle,
				true,
				setAlarmBinding.fragmentCreatealarmRecurring.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckMon.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckTue.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckWed.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckThu.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckFri.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckSat.isChecked(),
				setAlarmBinding.fragmentCreatealarmCheckSun.isChecked(),
				tone,
				isVibrate
		);
		createAlarmViewModel.update(updatedAlarm);
		updatedAlarm.schedule(this);
	}

	private void updateAlarmInfo(Alarm alarm) {
		setAlarmBinding.fragmentCreatealarmTitle.setText(alarm.getTitle());
		setAlarmBinding.fragmentCreatealarmTimePicker.setHour(alarm.getHour());
		setAlarmBinding.fragmentCreatealarmTimePicker.setMinute(alarm.getMinute());
		if (alarm.isRecurring()) {
			setAlarmBinding.fragmentCreatealarmRecurring.setChecked(true);
			setAlarmBinding.fragmentCreatealarmRecurringOptions.setVisibility(View.VISIBLE);
			if (alarm.isMonday())
				setAlarmBinding.fragmentCreatealarmCheckMon.setChecked(true);
			if (alarm.isTuesday())
				setAlarmBinding.fragmentCreatealarmCheckTue.setChecked(true);
			if (alarm.isWednesday())
				setAlarmBinding.fragmentCreatealarmCheckWed.setChecked(true);
			if (alarm.isThursday())
				setAlarmBinding.fragmentCreatealarmCheckThu.setChecked(true);
			if (alarm.isFriday())
				setAlarmBinding.fragmentCreatealarmCheckFri.setChecked(true);
			if (alarm.isSaturday())
				setAlarmBinding.fragmentCreatealarmCheckSat.setChecked(true);
			if (alarm.isSunday())
				setAlarmBinding.fragmentCreatealarmCheckSun.setChecked(true);
			tone = alarm.getTone();
			ringtone = RingtoneManager.getRingtone(this, Uri.parse(tone));
			setAlarmBinding.fragmentCreatealarmSetToneName.setText(ringtone.getTitle(this));
			if (alarm.isVibrate())
				setAlarmBinding.fragmentCreatealarmVibrateSwitch.setChecked(true);
		}
	}
}
