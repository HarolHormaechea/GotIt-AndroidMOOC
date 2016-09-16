package com.hhg.gotit.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hhg.gotit.MainActivity;
import com.hhg.gotit.R;


public class LogIn extends Fragment {
	private MainActivity activity;
	private EditText username, password;
	private Button submitButton;
	
	public LogIn(MainActivity activity){
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.login_fragment_view, container, false);
	    username = (EditText) view.findViewById(R.id.loginUsernameField);
	    password = (EditText) view.findViewById(R.id.loginPwdField);
	    submitButton = (Button) view.findViewById(R.id.loginSubmit);
	    
	    submitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.i(getTag(), "Entering onClick()");
				activity.getOpsManager().login(
						username.getText().toString(), 
						password.getText().toString());
			}
	    	
	    });
	    
		return view;
	}
}
