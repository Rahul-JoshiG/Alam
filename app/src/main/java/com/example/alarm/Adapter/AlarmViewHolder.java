package com.example.alarm.Adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarm.R;
import com.example.alarm.database.Alarm;
import com.example.alarm.databinding.AlarmShowBinding;
import com.example.alarm.util.DayUtil;
import com.example.alarm.util.OnToggleAlarmListener;


public class AlarmViewHolder extends RecyclerView.ViewHolder {
	private final TextView alarmTime;
	private final ImageView alarmRecurring;
	private final TextView alarmRecurringDays;
	private final TextView alarmTitle;
	@SuppressLint("UseSwitchCompatOrMaterialCode")
	private final Switch alarmStarted;
	private final ImageButton deleteAlarm;
	private final View itemView;
	private final TextView alarmDay;
	public AlarmViewHolder(@NonNull AlarmShowBinding itemAlarmBinding) {
		super(itemAlarmBinding.getRoot());
		alarmTime = itemAlarmBinding.itemAlarmTime;
		alarmStarted = itemAlarmBinding.itemAlarmStarted;
		alarmRecurring = itemAlarmBinding.itemAlarmRecurring;
		alarmRecurringDays = itemAlarmBinding.itemAlarmRecurringDays;
		alarmTitle = itemAlarmBinding.itemAlarmTitle;
		deleteAlarm= itemAlarmBinding.itemAlarmRecurringDelete;
		alarmDay = itemAlarmBinding.itemAlarmDay;
		this.itemView=itemAlarmBinding.getRoot();
	}

	@SuppressLint("SetTextI18n")
	public void bind(Alarm alarm, OnToggleAlarmListener listener) {
		@SuppressLint("DefaultLocale") String alarmText = String.format("%02d:%02d", alarm.getHour(), alarm.getMinute());

		alarmTime.setText(alarmText);
		alarmStarted.setChecked(alarm.isStarted());

		if (alarm.isRecurring()) {
			alarmRecurring.setImageResource(R.drawable.baseline_event_repeat_24);
			alarmRecurringDays.setText(alarm.getRecurringDaysText());
		} else {
			alarmRecurring.setImageResource(R.drawable.baseline_looks_one_24);
			alarmRecurringDays.setText("Once Off");
		}

		if (!alarm.getTitle().isEmpty()) {
			alarmTitle.setText(alarm.getTitle());
		} else {
			alarmTitle.setText("My alarm");
		}
		if(alarm.isRecurring()){
			alarmDay.setText(alarm.getRecurringDaysText());
		}
		else {
			alarmDay.setText(DayUtil.getDay(alarm.getHour(),alarm.getMinute()));
		}
		alarmStarted.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if(buttonView.isShown() || buttonView.isPressed())
				listener.onToggle(alarm);
		});

		deleteAlarm.setOnClickListener(view -> listener.onDelete(alarm));

		itemView.setOnClickListener(view -> listener.onItemClick(alarm,view));
	}
}
