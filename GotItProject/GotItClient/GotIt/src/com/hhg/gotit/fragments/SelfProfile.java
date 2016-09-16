package com.hhg.gotit.fragments;


import java.io.File;

import retrofit.mime.TypedFile;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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


public class SelfProfile extends Fragment {
	private static final int TAKE_IMAGE = 225;
	private MainActivity activity;
	private TextView username, name, medical;
	private Button uploadPhoto;
	private ImageView image;
	private File localTakenImage;
	
	public SelfProfile(MainActivity activity){
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.profile_frament_view, container, false);
	    username = (TextView) view.findViewById(R.id.profile_username);
	    name = (TextView) view.findViewById(R.id.profile_firstandlastname);
	    medical = (TextView) view.findViewById(R.id.profile_medicalnumber);
	    uploadPhoto = (Button) view.findViewById(R.id.profile_upload_photo);
	    image = (ImageView) view.findViewById(R.id.profile_image);
	    activity.getOpsManager().getUserData(); //We request the data to be presented in this fragment
		
	    uploadPhoto.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File externalDir = Environment.getExternalStoragePublicDirectory(
			            Environment.DIRECTORY_PICTURES);
				
				localTakenImage = new File(externalDir, "selfImage.jpg");
				takePicture.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(localTakenImage));
				
				startActivityForResult(takePicture, TAKE_IMAGE);    
			}
	    	
	    });
	    
	    return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, 
		       Intent imageReturnedIntent) {
		    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

		    switch(requestCode) { 
		    case TAKE_IMAGE:
		        if(resultCode == Activity.RESULT_OK){  

		            Bitmap pickedImage = BitmapFactory.decodeFile(localTakenImage.getAbsolutePath());
		            
		            setImage(pickedImage);		           
		            
		            activity.getOpsManager().uploadProfilePicture(
		            		new TypedFile("image/jpg",localTakenImage));
		        }
		    }
		}
	
	public void initFields(UserData userData){
		username.setText(userData.getUsername());
		name.setText(userData.getFirstName()+" "+userData.getLastName());
		medical.setText(String.valueOf(userData.getMedicalRecordNumber()));
		if(userData.getUserType() == null)
			Log.d("UserList", "USERTYPENULL");
		activity.getOpsManager().downloadProfilePicture(userData.getUsername());
	}
	
	public void setImage(Bitmap photo){
		image.setImageBitmap(photo);
	}
}
