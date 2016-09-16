package com.hhg.gotit.fragments;

import com.hhg.gotit.MainActivity;
import com.hhg.gotit.OperationsManager.NOTIFICATION_FREQ;
import com.hhg.gotit.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class ConfigScreen extends Fragment {
	private MainActivity activity;
	
	public ConfigScreen(MainActivity activity){
		this.activity = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.config_fragment, container, false);
		Switch shareDataSwitch = 
				(Switch) view.findViewById(R.id.config_switch_share_quiz);
		shareDataSwitch.setChecked(
				activity.getOpsManager()
					.getLoggedUser().getAllowsFollowers());
		shareDataSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				activity.getOpsManager().setOwnFollowableStatus(isChecked);
			}
			
		});
		
		Switch notifFrequencySwitch = 
				(Switch) view.findViewById(R.id.config_switch_change_notif_freq);
		if(activity.getOpsManager()
				.getConfiguredNotificationFrequency() == NOTIFICATION_FREQ.HOURS6){
			notifFrequencySwitch.setChecked(true);
		}else{
			notifFrequencySwitch.setChecked(false);
		}
		notifFrequencySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					activity.getOpsManager().setAlarm(NOTIFICATION_FREQ.HOURS6);
				}else{
					activity.getOpsManager().setAlarm(NOTIFICATION_FREQ.HOURS8);
				}
			}
			
		});
		
		return view;
	}
	
}
