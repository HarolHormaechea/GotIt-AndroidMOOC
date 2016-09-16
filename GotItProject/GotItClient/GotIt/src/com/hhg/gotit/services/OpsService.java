package com.hhg.gotit.services;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hhg.gotit.AlarmReceiver;
import com.hhg.gotit.models.GotItApi;
import com.hhg.gotit.models.GotItApi.OP_CODE;
import com.hhg.gotit.models.GotItApi.USER_TYPE;
import com.hhg.gotit.models.Notification;
import com.hhg.gotit.models.Quiz;
import com.hhg.gotit.models.UserData;
import com.hhg.gotit.rest.SecuredRestBuilder;
import com.hhg.gotit.rest.UnsafeHttpsClient;


public class OpsService extends Service implements ErrorHandler{
	protected static enum OPERATIONS {NONE, LOGING_IN, CREATING_USER, UPLOADING_QUIZ,
		FOLLOWING_USER, UNFOLLOWING_USER, REQ_USER_LIST, CHANGING_FOLLOWABLE_STATUS,
		RETRIEVING_DATA}
	public static enum ALARM_FREQUENCY {TEST_FREQ, THREE_TIMES_A_DAY, FOUR_TIMES_A_DAY}
	
	private String SERVER_URL = "https://192.168.111.1:8443";
	private IBinder binderInterface = new BoundNetworkServiceBinder();
	private OpsServiceListener listener;
	private GotItApi secureApi;
	private String CLIENT_ID = "mobile";
	private String TAG = "OpsService";
	private UserData loggedUser;
	
	public UserData getLoggedUser() {
		return loggedUser;
	}

	public static String PREF_TAG = "preferences";
	public static String PREF_ALARM_TAG = "alarm";

	@Override
	public IBinder onBind(Intent intent) {
		return binderInterface;
	}
	
//	/**
//	 * Constructor, will create an alarm based on the user preferences, or
//	 * in test mode if no preferences were set beforehand in any prior 
//	 * session by the user.
//	 */
//	public OpsService(){
//		SharedPreferences prefs = this.getSharedPreferences(PREF_TAG, Context.MODE_PRIVATE);
//		if(!prefs.contains(PREF_ALARM_TAG)){
//			Editor editor = prefs.edit();
//			editor.putString(PREF_ALARM_TAG, ALARM_FREQUENCY.TEST_FREQ.toString());
//			editor.apply();
//			
//		}else{
//			String alarmSetting = prefs.getString(PREF_ALARM_TAG, null);
//			if(ALARM_FREQUENCY.FOUR_TIMES_A_DAY.toString().equals(alarmSetting))
//				setUpAlarm(ALARM_FREQUENCY.FOUR_TIMES_A_DAY);
//			else if(ALARM_FREQUENCY.THREE_TIMES_A_DAY.toString().equals(alarmSetting))
//				setUpAlarm(ALARM_FREQUENCY.THREE_TIMES_A_DAY);
//			else //if there is some screw up...
//				setUpAlarm(ALARM_FREQUENCY.TEST_FREQ);
//		}
//	}
	
	/**
	 * Method which performs a login with the requested credentials.
	 * Will return the USER_TYPE object which better defines the user
	 * authorities on the server, or null if the login was unsuccesfull.
	 * 
	 * @param user
	 * @param pwd
	 */
	public void login(final String user, final String pwd){
		new Thread(new Runnable(){

			@Override
			public void run() {
				USER_TYPE loggedUserType = null;
				try{
					secureApi = new SecuredRestBuilder()
					.setLoginEndpoint(SERVER_URL + GotItApi.TOKEN_PATH)
					.setUsername(user)
					.setPassword(pwd)
					.setClientId(CLIENT_ID)
					.setErrorHandler(OpsService.this)
					.setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
					.setEndpoint(SERVER_URL).setLogLevel(LogLevel.NONE).build()
					.create(GotItApi.class);
					loggedUserType = secureApi.getAccessLevelForUser();
					notifyLoginResult(loggedUserType);
				}catch(Exception ex){
					loggedUserType = null;
				}
			}
		}).start();
	}
	
	public void getLoggedUserData(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				UserData result = null; 
				try{
					result = secureApi.getMyData();
					notifyUserDataRetrieval(result);
				}catch(Exception ex){
					ex.printStackTrace();
					Log.e(TAG, "****ERROR: When attempting to retrieve user data.");
				}
			}
		}).start();
	}
	

	public void registerNewUser(final String username, final String pwd, 
			final String firstName, final String lastName, 
			final int medicalRecordNumber, final boolean allowsFollowers,
			final USER_TYPE userType){
		new Thread(new Runnable(){
			@Override
			public void run() {
				Boolean result = false; 
				try{
					RestAdapter unsecureRestAdapter = new RestAdapter.Builder()
			        .setLogLevel(RestAdapter.LogLevel.FULL)
			        .setEndpoint(SERVER_URL)
			        .setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
			        .build();
					
					GotItApi unsecureApi = unsecureRestAdapter.create(GotItApi.class);
					
					result = unsecureApi.signUp(username, pwd, firstName,
							lastName, medicalRecordNumber, allowsFollowers, userType);
				}catch(Exception ex){
					Log.e(TAG, "****ERROR: When attempting to register a new user.");
					result = false;
				}finally{
					notifySignUpAttemptResult(result);
				}
				
			}
		}).start();
	}
	
	
	public void follow(final String userToFollow){
		new Thread(new Runnable(){
			@Override
			public void run() {
				OP_CODE result;
				try{
					result = secureApi.followTeen(userToFollow);
					notifyFollowAttemptResult(result);
				}catch(Exception ex){
					Log.e(TAG, "****ERROR: When attempting to follow a new user.");
					ex.printStackTrace();
				}
			}
		}).start();
	}
	
	public void stopFollow(final String userToStopFollowing){
		new Thread(new Runnable(){
			@Override
			public void run() {
				OP_CODE result;
				try{
					result = secureApi.stopFollowingTeen(userToStopFollowing);
					notifyStopFollowAttemptResult(result);
				}catch(Exception ex){
					Log.e(TAG, "****ERROR: When attempting to stop following an user.");
				}
			}
		}).start();
	}
	
	
	public void requestFollowableUserList(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				Collection<UserData> users;
				try{
					users = secureApi.getTeenList();
					notifyUserListRequestResult(users);
				}catch(Exception ex){
					Log.e(TAG, "****ERROR: When attempting request user list.");
				}
			}
		}).start();
	}
	
	public void changeIsFollowableStatus(final boolean followable){
		new Thread(new Runnable(){
			@Override
			public void run() {
				boolean status;
				try{
					status = secureApi.setFollowableStatus(followable);
					notifyFollowableStatusChanged(status);
				}catch(Exception ex){
					Log.e(TAG, "****ERROR: When attempting to change followable status.");
				}
			}
		}).start();
	}
	
	public void requestQuiz()
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Quiz quiz;
				try{
					quiz = secureApi.requestNewQuiz();
					notifyNewQuizReceived(quiz);
				}catch(Exception ex){
					ex.printStackTrace();
					Log.e(TAG, "****ERROR: When attempting to retrieve a new quiz.");
				}
			}
		}).start();
	}
	
	
	public void postAnsweredQuiz(final Quiz quiz){
		new Thread(new Runnable(){
			@Override
			public void run() {
				Boolean result;
				try{
					result = secureApi.uploadCompletedQuiz(quiz);
					notifyUploadQuizResult(result);
				}catch(Exception ex){
					Log.e(TAG, "****ERROR: When attempting to retrieve a new quiz.");
				}
			}
		}).start();
	}
	
	
	public void fetchAllNotifications()
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Collection<Notification> result;
				try{
					result = secureApi.getPendingNotifications();
					notifyRequestNotificationsResult(result);
				}catch(Exception ex){
					Log.e(TAG, "****ERROR: When attempting to retrieve a new quiz.");
				}
			}
		}).start();
	}
	
	public void fetchSingleNotification(final int id)
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				Quiz result;
				try{
					result = secureApi.getNotificationObject(id);
					notifyRequestNotificationObjectResult(result);
				}catch(Exception ex){
					Log.e(TAG, "****ERROR: When attempting to retrieve a new quiz.");
				}
			}
		}).start();
	}

	public void uploadProfilePicture(final TypedFile image){
		new Thread(new Runnable(){
			@Override
			public void run() {
				boolean result;
				try{
					result = secureApi.uploadPicture(image);
					notifyPhotoUploadResult(result);
				}catch(Exception ex){
					Log.e(TAG, "****ERROR: When attempting to upload a photo.");
				}
			}
		}).start();
	}
	
	public void downloadProfilePicture(final String username){
		new Thread(new Runnable(){
			@Override
			public void run() {
				Response result;
				try{
					result = secureApi.getProfilePicture(username);
					Bitmap bitmap = BitmapFactory.decodeStream(result.getBody().in());
					notifyPhotoDownloadResult(bitmap);
				}catch(Exception ex){
					Log.e(TAG, "****ERROR: When attempting to download a photo.");
				}
			}
		}).start();
	}


	/**
	 * Binder class for our service.
	 * 
	 * @author Harold
	 *
	 */
	public class BoundNetworkServiceBinder extends Binder{
		public OpsService getService() {
			return OpsService.this;
		}
	}
	
	protected void notifySignUpAttemptResult(boolean result){
		listener.onRegisterNewUserResult(result);
	}
	


	private void notifyFollowAttemptResult(OP_CODE result) {
		listener.onFollowAttemptResult(result);
	}
	
	private void notifyStopFollowAttemptResult(OP_CODE result) {
		listener.onStopFollowAttemptResult(result);
	}
	
	protected void notifyLoginResult(USER_TYPE userType){
		Log.i(TAG, "Service is notifying application about the login result.");
		listener.onLoginResult(userType);
	}
	
	protected void notifyUserListRequestResult(Collection<UserData> users) {
		listener.onFollowableUserListRequestResult(users);
	}
	
	protected void notifyFollowableStatusChanged(boolean status) {
		
		listener.onFollowableSelfStatusChange(status);
	}

	protected void notifyUserDataRetrieval(UserData result) {
		this.loggedUser = result;
		listener.onCurrentUserDataRequestResult(result);
	}
	
	protected void notifyNewQuizReceived(Quiz q){
		listener.onNewQuizReceived(q);
	}
	
	protected void notifyUploadQuizResult(boolean result){
		listener.onUploadAttemptResult(result);
	}
	

	protected void notifyRequestNotificationsResult(
			Collection<Notification> result) {
		listener.onRequestAllNotificationsResult(result);
	}
	
	protected void notifyPhotoUploadResult(boolean result){
		listener.onProfileUploadRequestResult(result);
	}
	
	protected void notifyPhotoDownloadResult(Bitmap image){
		listener.onProfilePictureDownloadRequestResult(image);
	}
	

	protected void notifyRequestNotificationObjectResult(Quiz result) {
		listener.onRequestNotificationObjectResult(result);
	}
	
	public void registerListener(OpsServiceListener listener) {
		this.listener = listener;
	}
	
	

	@Override
	public Throwable handleError(RetrofitError arg0) {
		Log.d(TAG, "Handling error by the service.");
		listener.onUnexpectedError(arg0.getMessage());
		return arg0;
	}
	
	/**
	 * Sets up the alarm which will force the app to show up so the user
	 * can log in and fill a new test or whatever!
	 * 
	 * @param freq
	 */
	public void setUpAlarm(ALARM_FREQUENCY freq){
		
		AlarmManager alarmManager = 
				(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		//First, we have to destroy old alarms to avoid problems.
		alarmManager.cancel(alarmIntent);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 0);
		String preferenceFreq = null;
		long frequency = 1000 * 60 * 60 * 8; //8 Hrs default delay between alarms
		switch(freq){
		case FOUR_TIMES_A_DAY:
			frequency = 1000 * 60 * 60 * 6;
			preferenceFreq = ALARM_FREQUENCY.FOUR_TIMES_A_DAY.toString();
			break;
		case THREE_TIMES_A_DAY:
			frequency = 1000 * 60 * 60 * 8;
			preferenceFreq = ALARM_FREQUENCY.THREE_TIMES_A_DAY.toString();
			break;
		case TEST_FREQ:
			frequency = 1000 * 5; //30s frequency for tests
			SharedPreferences prefs = this.getSharedPreferences(PREF_TAG, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putString(PREF_ALARM_TAG, ALARM_FREQUENCY.TEST_FREQ.toString());
			editor.apply();
			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					frequency,
					frequency*2,
					alarmIntent);
			return;
		}
		
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), frequency, alarmIntent);
		SharedPreferences prefs = this.getSharedPreferences(PREF_TAG, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(PREF_ALARM_TAG, preferenceFreq);
		editor.apply();
		
	}
	
	public ALARM_FREQUENCY getAlarmConfig(){
		ALARM_FREQUENCY returnFreq = null;
		String alarmSetting = getSharedPreferences(PREF_TAG, Context.MODE_PRIVATE)
				.getString(PREF_ALARM_TAG, null);
		if(ALARM_FREQUENCY.FOUR_TIMES_A_DAY.toString().equals(alarmSetting))
			returnFreq = ALARM_FREQUENCY.FOUR_TIMES_A_DAY;
		else if(ALARM_FREQUENCY.THREE_TIMES_A_DAY.toString().equals(alarmSetting))
			returnFreq = ALARM_FREQUENCY.THREE_TIMES_A_DAY;
		else //if there is some screw up...
			returnFreq = ALARM_FREQUENCY.TEST_FREQ;

		return returnFreq;
	}
	
}
