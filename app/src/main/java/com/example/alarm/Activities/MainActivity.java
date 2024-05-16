package com.example.alarm.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarm.Adapter.AlarmRecyclerViewAdapter;
import com.example.alarm.R;
import com.example.alarm.database.Alarm;
import com.example.alarm.databinding.ActivityMainBinding;
import com.example.alarm.util.OnToggleAlarmListener;
import com.example.alarm.viewmodel.AlarmListViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnToggleAlarmListener {

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private ActivityMainBinding mainBinding;

	private AlarmListViewModel alarmsListViewModel;
	private AlarmRecyclerViewAdapter alarmRecyclerViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		setDateTime();

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = sharedPreferences.edit();

		setupAlarmRecyclerView();

		mainBinding.addNewAlarm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SetAlarmActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setupAlarmRecyclerView() {
		alarmRecyclerViewAdapter = new AlarmRecyclerViewAdapter(this);
		alarmsListViewModel = new ViewModelProvider(this).get(AlarmListViewModel.class);
		alarmsListViewModel.getAlarmsLiveData().observe(this, new Observer<List<Alarm>>() {
			@Override
			public void onChanged(List<Alarm> alarms) {
				if (alarms != null) {
					alarmRecyclerViewAdapter.setAlarms(alarms);
				}
			}
		});

		RecyclerView alarmsRecyclerView = mainBinding.recyclerView;
		alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
		alarmsRecyclerView.setAdapter(alarmRecyclerViewAdapter);
	}

	private void setDateTime() {
		Calendar calendar = Calendar.getInstance();
		@SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
		@SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy");
		mainBinding.currentDate.setText(dateFormat.format(calendar.getTime()));
		mainBinding.currentTime.setText(timeFormat.format(calendar.getTime()));

		mainBinding.currentTime.postDelayed(new Runnable() {
			@Override
			public void run() {
				setDateTime();
			}
		}, 1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.dayNightMode) {
			boolean isDayNightMode = sharedPreferences.getBoolean(getString(R.string.dayNightTheme), true);
			int nightMode = isDayNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
			AppCompatDelegate.setDefaultNightMode(nightMode);
			editor.putBoolean(getString(R.string.dayNightTheme), !isDayNightMode).apply();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onToggle(Alarm alarm) {
		if (alarm.isStarted()) {
			alarm.cancelAlarm(getApplicationContext());
		} else {
			alarm.schedule(getApplicationContext());
		}
		alarmsListViewModel.update(alarm);
	}

	@Override
	public void onDelete(Alarm alarm) {
		if (alarm.isStarted())
			alarm.cancelAlarm(getApplicationContext());
		alarmsListViewModel.delete(alarm.getAlarmId());
	}

	@Override
	public void onItemClick(Alarm alarm, View view) {
		if (alarm.isStarted())
			alarm.cancelAlarm(view.getContext());
	}
}
