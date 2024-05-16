package com.example.alarm.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.alarm.database.Alarm;
import com.example.alarm.databinding.AlarmShowBinding;
import com.example.alarm.util.OnToggleAlarmListener;

import java.util.ArrayList;
import java.util.List;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
	private List<Alarm> alarms;
	private final OnToggleAlarmListener listener;

	public AlarmRecyclerViewAdapter(OnToggleAlarmListener listener) {
		this.alarms = new ArrayList<>();
		this.listener = listener;
	}

	@NonNull
	@Override
	public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		com.example.alarm.databinding.AlarmShowBinding alarmShowBinding = AlarmShowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
		return new AlarmViewHolder(alarmShowBinding);
	}

	@Override
	public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
		Alarm alarm = alarms.get(position);
		holder.bind(alarm, listener);
	}

	@Override
	public int getItemCount() {
		return alarms.size();
	}

	@SuppressLint("NotifyDataSetChanged")
	public void setAlarms(List<Alarm> alarms) {
		this.alarms = alarms;
		notifyDataSetChanged();
	}
}
