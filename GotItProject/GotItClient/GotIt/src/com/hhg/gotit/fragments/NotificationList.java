package com.hhg.gotit.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hhg.gotit.MainActivity;
import com.hhg.gotit.R;


public class NotificationList extends Fragment {
	private MainActivity activity;
	private EditText username, password;
	private Button submitButton;
	
	public NotificationList(MainActivity activity){
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.login_fragment_view, container, false);
	    
	    
		return view;
	}
}
