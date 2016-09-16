package com.hhg.gotit.fragments;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhg.gotit.MainActivity;
import com.hhg.gotit.R;
import com.hhg.gotit.models.UserData;


public class OtherProfile extends Fragment {
	private MainActivity activity;
	private TextView username, name, medical;
	private Button follow, unfollow;
	private ImageView image;
	private UserData userData;
	private Bitmap profilePicture;
	
	public OtherProfile(MainActivity activity, UserData userData){
		this.activity = activity;
		this.userData = userData;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.others_profile_fragment_view, container, false);
	    username = (TextView) view.findViewById(R.id.oprofile_username);
	    name = (TextView) view.findViewById(R.id.oprofile_firstandlastname);
	    medical = (TextView) view.findViewById(R.id.oprofile_medicalnumber);
	    image = (ImageView) view.findViewById(R.id.oprofile_image);
	    follow = (Button) view.findViewById(R.id.oprofile_follow);
	    unfollow = (Button) view.findViewById(R.id.oprofile_unfollow);
	    
	    follow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				activity.getOpsManager().followUser(userData.getUsername());
			}
	    	
	    });
	    
	    unfollow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				activity.getOpsManager().unfollowUser(userData.getUsername());
			}
	    	
	    });
	    
	    
	    initFields();
	    
		return view;
	}
	
	public void initFields(){
		username.setText(userData.getUsername());
		name.setText(userData.getFirstName()+" "+userData.getLastName());
		medical.setText(String.valueOf(userData.getMedicalRecordNumber()));
		activity.getOpsManager().downloadProfilePicture(userData.getUsername());
		
	}
	
	public void setImage(Bitmap photo){
		image.setImageBitmap(photo);
	}
}
