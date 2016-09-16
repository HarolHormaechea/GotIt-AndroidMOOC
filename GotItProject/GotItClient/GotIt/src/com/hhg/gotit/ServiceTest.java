package com.hhg.gotit;

import java.util.Collection;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hhg.gotit.models.GotItApi.OP_CODE;
import com.hhg.gotit.models.GotItApi.USER_TYPE;
import com.hhg.gotit.models.Notification;
import com.hhg.gotit.models.Quiz;
import com.hhg.gotit.models.UserData;
import com.hhg.gotit.services.OpsService;
import com.hhg.gotit.services.OpsService.BoundNetworkServiceBinder;
import com.hhg.gotit.services.OpsServiceListener;

public class ServiceTest extends Activity implements OpsServiceListener{
	private static String TAG ="ServiceTest";
	private OpsService mBoundService;
	private ServiceConnection mServiceConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			BoundNetworkServiceBinder binder = (BoundNetworkServiceBinder) service; 
			mBoundService = binder.getService();
			mBoundService.registerListener(ServiceTest.this);
			
			if(mBoundService != null){
				String text = "Service connected to activity.";
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
				Log.d(TAG, text);
			}
			else{
				String text = "Service connected to activity... but is null.";
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
				Log.d(TAG, text);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBoundService = null;
			String text = "Service disconnected from activity.";
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
			Log.d(TAG, text);
		}
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_test);
	}
	
	
	public void bindService(View v){
		Log.d(TAG, "Attempting to bind service...");
		Intent intent = new Intent(ServiceTest.this, OpsService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	
	public void testCreateNewUser(View v){
		Log.d(TAG, "Attempting to register new user...");
		mBoundService.registerNewUser("Android1", "pass",
				"Android ", " made", 944, true, USER_TYPE.TEEN);
	}
	
	public void testLogin(View v){
		mBoundService.login("teen1", "pass");
	}
	
	public void testFollow(View v){
		mBoundService.follow("teen2");
	}
	
	public void testStopFollow(View v){
		mBoundService.stopFollow("teen2");
	}
	
	public void testGetUserList(View v){
		mBoundService.requestFollowableUserList();
	}
	
	public void testSwitchFollowableStatus(View v){
		mBoundService.getLoggedUserData();
	}
	
	public void testRequestNewQuiz(View v){
		mBoundService.requestQuiz();
	}
	

	@Override
	public void onRegisterNewUserResult(boolean result) {
		final String text = "Has the new user been created succesfully?:  "+result;
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
	}


	@Override
	public void onLoginResult(USER_TYPE userType) {
		final String text = "Logged in with user level "+userType;
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
	}
	


	@Override
	public void onFollowAttemptResult(OP_CODE result) {
		final String text = "Follow attempt result: "+result;
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
	}

	

	@Override
	public void onStopFollowAttemptResult(OP_CODE result) {
		final String text = "Stop follow attempt result: "+result;
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
	}
	

	@Override
	public void onFollowableUserListRequestResult(Collection<UserData> userData) {
		final String text = "Number of users retrieved: "+userData.size();
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
		
	}
	
	@Override
	public void onFollowableSelfStatusChange(boolean updatedStatus) {
		final String text = "New followable status: "+updatedStatus;
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
	}
	
	@Override
	public void onCurrentUserDataRequestResult(UserData data) {
		mBoundService.changeIsFollowableStatus(!data.getAllowsFollowers());
	}


	@Override
	protected void onDestroy() {
		mBoundService.unbindService(mServiceConnection);
		super.onDestroy();
	}


	@Override
	public void onUnexpectedError(String errorCause) {
		final String text = "ERROR: "+errorCause;
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
	}


	@Override
	public void onNewQuizReceived(Quiz q) {
		final String text = "Number of questions in retrieved quiz: "+q.getQuestions().size();
		
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
		
		int i = 0;
		for(String question : q.getQuestions()){
			switch(q.getReplyDataTypes().get(i)){
			case FLOAT:
				q.getReplies().set(i, String.valueOf(0.5f));
				break;
			case STRING:
				q.getReplies().set(i, "TEST");
				break;
			}
			i++;
		}
		mBoundService.postAnsweredQuiz(q);
		
		
	}


	@Override
	public void onUploadAttemptResult(boolean status) {
		final String text = "Upload relply quiz result: "+status;
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
	}


	@Override
	public void onRequestAllNotificationsResult(Collection<Notification> result) {
		final String text = "Number of notifications retrieved: "+result.size();
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
	}


	@Override
	public void onRequestNotificationObjectResult(Quiz result) {
		final String text = "Author of requested quiz: "+result.getAuthor().getFirstName();
		this.runOnUiThread(new Runnable(){
			@Override
					public void run() {
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
					}});
		Log.d(TAG, text);
	}


	@Override
	public void onProfilePictureDownloadRequestResult(Bitmap image) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProfileUploadRequestResult(boolean result) {
		// TODO Auto-generated method stub
		
	}
}
