package com.hhg.gotit.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.hhg.gotit.MainActivity;
import com.hhg.gotit.R;
import com.hhg.gotit.models.GotItApi.USER_TYPE;

/**
 * Fragment tasked with handling of users sign-up. It throws the ball
 * at the operations manager (provided by the activity) so it can
 * perform any basic required checks.
 * 
 * @author Harold
 *
 */
public class SignUp extends Fragment {
	private EditText username, firstName, lastName, password, record;
	private RadioGroup radioGroup;
	private Button submitButton;
	private CheckBox shareData;
	private MainActivity activity;
	
	public SignUp(MainActivity activity){
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.register_user_fragment_view, container, false);
	    username = (EditText) view.findViewById(R.id.signUpUsername);
	    firstName = (EditText) view.findViewById(R.id.signUpFirstName);
	    lastName = (EditText) view.findViewById(R.id.signUpLastName);
	    password = (EditText) view.findViewById(R.id.signUpPassword);
	    record = (EditText) view.findViewById(R.id.signUpMedicalNumber);
	    radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupUserType);
	    submitButton = (Button) view.findViewById(R.id.signUpSubmitButton);
	    shareData = (CheckBox) view.findViewById(R.id.signUpAllowFollowers);
	    
	    submitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.i(getTag(), "Entering onClick() "+radioGroup.getCheckedRadioButtonId());
				USER_TYPE userType = null;
				if(radioGroup.getCheckedRadioButtonId() == -1){
					return;
				}else if(radioGroup.getCheckedRadioButtonId() == R.id.signUpTeenType){
					userType = USER_TYPE.TEEN;
				}else if(radioGroup.getCheckedRadioButtonId() == R.id.signUpFollowerType){
					userType = USER_TYPE.FOLLOWER;
				}
				
				try {
					activity.getOpsManager().registerUser(
							username.getText().toString(),
							password.getText().toString(),
							firstName.getText().toString(),
							lastName.getText().toString(),
							Integer.parseInt(record.getText().toString()),
							shareData.isChecked(),
							userType);
				} catch (NumberFormatException e) {
					Log.i(getTag(), "NumberFormatException");
					activity.postUiMessage("Invalid medical record number");
				} catch (Exception e) {
					Log.i(getTag(), "Exception "+e.getMessage());
					activity.postUiMessage(e.getMessage());
				}
			}
	    	
	    });
	    
		return view;
	}
	
}
