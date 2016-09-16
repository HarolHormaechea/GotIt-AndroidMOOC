package com.hhg.gotit;

import java.util.Collection;

import retrofit.mime.TypedFile;
import android.graphics.Bitmap;

import com.hhg.gotit.models.GotItApi.OP_CODE;
import com.hhg.gotit.models.GotItApi.USER_TYPE;
import com.hhg.gotit.models.Notification;
import com.hhg.gotit.models.Quiz;
import com.hhg.gotit.models.UserData;
import com.hhg.gotit.services.OpsService;
import com.hhg.gotit.services.OpsService.ALARM_FREQUENCY;
import com.hhg.gotit.services.OpsServiceListener;

public class OperationsManager implements OpsServiceListener {
	public enum OPERATION_TYPE{REGISTER_USER, LOGIN, GET_PROFILE,
		REQ_USER_LIST, FOLLOW, STOP_FOLLOW, OTHERS_PROFILE,
		FETCH_NOTIFICATIONS, SHOW_QUIZ, POST_QUIZ, GET_NEW_QUIZ, PHOTO_DOWNLOAD, PHOTO_UPLOAD}
	public enum OPERATION_RESULT{OK, ERROR}
	public enum NOTIFICATION_FREQ{HOURS6, HOURS8, TEST}
	
	private static String TAG ="OperationsManager";
	private OpsService mBoundService;
	private MainActivity activity;
	private USER_TYPE loggedInUserAccessAuthority;

	
	public OperationsManager(MainActivity activity){
		this.activity = activity;
		mBoundService = activity.getmBoundService();
	}
	
	public void setAlarm(NOTIFICATION_FREQ freq){
		switch(freq){
		case HOURS6:
			mBoundService.setUpAlarm(OpsService.ALARM_FREQUENCY.FOUR_TIMES_A_DAY);
			activity.postUiMessage("Alarm set to every 6 hours.");
			break;
		case HOURS8:
			mBoundService.setUpAlarm(OpsService.ALARM_FREQUENCY.THREE_TIMES_A_DAY);
			activity.postUiMessage("Alarm set to every 8 hours.");
			break;
		case TEST:
			mBoundService.setUpAlarm(OpsService.ALARM_FREQUENCY.TEST_FREQ);
			activity.postUiMessage("Alarm set to every test mode.");
		}
	}
	
	public NOTIFICATION_FREQ getConfiguredNotificationFrequency(){
		NOTIFICATION_FREQ freq = null;
		switch (mBoundService.getAlarmConfig()){
		case FOUR_TIMES_A_DAY:
			freq = NOTIFICATION_FREQ.HOURS6;
			break;
		case TEST_FREQ:
			freq = NOTIFICATION_FREQ.TEST;
			break;
		case THREE_TIMES_A_DAY:
			freq = NOTIFICATION_FREQ.HOURS8;
			break;
		default:
			freq = null;
		}
		return freq;
	}
	
	public void registerUser(String username, String password, String firstName, 
			String lastName, int recordNumber, boolean allowsFollowers, USER_TYPE userType) throws Exception{
		
		if(!UserData.validateName(firstName) 
				|| !UserData.validateName(lastName) 
				|| !UserData.validateName(username)){
			throw new Exception("User data fields are not valid.");
		}else if(recordNumber <= 0){
			throw new Exception("Invalid medical record number.");
		}else{
			mBoundService.registerNewUser(username, password, firstName, lastName, recordNumber, allowsFollowers, userType);
		}		
	}
	
	public void login(String username, String password){
		mBoundService.login(username, password);
	}
	
	public void getUserData(){
		mBoundService.getLoggedUserData();
	}
	
	
	public void followUser(String username){
		mBoundService.follow(username);
	}
	
	public void unfollowUser(String username){
		mBoundService.stopFollow(username);
	}
	
	public void getFollowableUsers(){
		mBoundService.requestFollowableUserList();
	}

	public void getPendingNotifications(){
		mBoundService.fetchAllNotifications();
	}
	
	public void getNewQuiz(){
		mBoundService.requestQuiz();
	}

	public void postQuiz(Quiz q){
		mBoundService.postAnsweredQuiz(q);
	}
	
	public void downloadProfilePicture(String username){
		mBoundService.downloadProfilePicture(username);
	}
	
	public void uploadProfilePicture(TypedFile image){
		mBoundService.uploadProfilePicture(image);
	}
	
	public void setOwnFollowableStatus(boolean followable){
		mBoundService.changeIsFollowableStatus(followable);
	}
	
	
	@Override
	public void onRegisterNewUserResult(boolean result) {
		if(!result){
			activity.processResult(OPERATION_TYPE.REGISTER_USER,
					OPERATION_RESULT.ERROR, "User creation failed. May have another "
							+ "user with the same username in database.");
		}else{
			activity.processResult(OPERATION_TYPE.REGISTER_USER,
					OPERATION_RESULT.OK, "User creation success.");
		}
	}

	@Override
	public void onLoginResult(USER_TYPE userType) {
		loggedInUserAccessAuthority = userType;
		if(userType == null){
			activity.processResult(OPERATION_TYPE.LOGIN, OPERATION_RESULT.ERROR,
					"Login failed, check credentials and try again.");
		}else{
			activity.processResult(OPERATION_TYPE.LOGIN, OPERATION_RESULT.OK,
					"Login success, as a "+userType.toString());
			activity.setUIMode(userType);
		}
		
	}

	@Override
	public void onCurrentUserDataRequestResult(UserData data) {
		if(data != null){
			activity.processResult(OPERATION_TYPE.GET_PROFILE, OPERATION_RESULT.OK,
					"Profile retrieved", data);
		}else{
			activity.processResult(OPERATION_TYPE.GET_PROFILE, OPERATION_RESULT.ERROR,
					"Profile could not be retrieved.", null);
		}
		
	}

	@Override
	public void onFollowAttemptResult(OP_CODE result) {
		OPERATION_TYPE opType  = OPERATION_TYPE.FOLLOW;
		String message = "";
		OPERATION_RESULT status = OPERATION_RESULT.ERROR;
		switch(result){
		case FOLLOW_RELATIONSHIP_ALREADY_EXISTS:
			status = OPERATION_RESULT.ERROR; 
			message = "You are already following this user.";
			break;
		case FOLLOW_RELATIONSHIP_CREATED:
			status = OPERATION_RESULT.OK;
			message = "Yus! You are now following this user!";
			break;
		case FOLLOW_RELATIONSHIP_ERROR:
			status = OPERATION_RESULT.ERROR;
			message = "Unknown error when attempting to follow user.";
			break;
		case FOLLOW_RELATIONSHIP_NOT_ALLOWED:
			status = OPERATION_RESULT.ERROR;
			message = "The user does not allow followers.";
			break;
		default:
			status = OPERATION_RESULT.ERROR;
			message = "Unexpected result for this operation.";
			break;
		}
		activity.processResult(opType, status, message, (Object[])null);
	}

	@Override
	public void onStopFollowAttemptResult(OP_CODE result) {
		OPERATION_TYPE opType  = OPERATION_TYPE.FOLLOW;
		String message = "";
		OPERATION_RESULT status = OPERATION_RESULT.ERROR;
		switch(result){
		case FOLLOW_RELATIONSHIP_ERROR:
			status = OPERATION_RESULT.ERROR;
			message = "Unknown error when attempting to stop following the user.";
			break;
		case FOLLOW_RELATIONSHIP_REMOVED:
			status = OPERATION_RESULT.OK;
			message = "The follow relationship has been removed.";
			break;
		case FOLLOW_RELATIONSHIP_DOES_NOT_EXIST:
			status = OPERATION_RESULT.ERROR;
			message = "You were not following this user..";
			break;
		default:
			status = OPERATION_RESULT.ERROR;
			message = "Unexpected result for this operation.";
			break;
		}
		activity.processResult(opType, status, message, (Object[])null);
	}

	@Override
	public void onFollowableUserListRequestResult(Collection<UserData> userData) {
		activity.processResult(OPERATION_TYPE.REQ_USER_LIST,
				OPERATION_RESULT.OK, "Data retrieved", userData);
	}

	@Override
	public void onFollowableSelfStatusChange(boolean updatedStatus) {
		String message = "";
		if(updatedStatus){
			message = "Your followers will now receive notifications about your quizes.";
		}else{
			message = "Your followers will no longer receive notifications about you.";
		}
		activity.postUiMessage(message);
	}

	@Override
	public void onNewQuizReceived(Quiz q) {
		OPERATION_RESULT opRes = null;
		String message = null;
		if(q != null && q.getQuestions() != null 
				&& q.getQuestions().size() > 0){
			opRes = OPERATION_RESULT.OK;
			message = "New quiz retrieved";
		}else{
			opRes = OPERATION_RESULT.ERROR;
			message = "Error on quiz download attempt.";
		}
		activity.processResult(OPERATION_TYPE.GET_NEW_QUIZ,
				opRes, 
				message,
				q);
	}

	@Override
	public void onUploadAttemptResult(boolean status) {
		OPERATION_RESULT opRes;
		String message;
		
		if(status){
			opRes = OPERATION_RESULT.OK;
			message = "Upload successful";
		}else{
			opRes = OPERATION_RESULT.ERROR;
			message = "Error on quiz upload attempt.";
		}
		
		activity.processResult(OPERATION_TYPE.POST_QUIZ,
				opRes, 
				message,
				(Object)null);
		
	}

	@Override
	public void onUnexpectedError(String errorCause) {
		if(errorCause.contains("401"))
			activity.postUiMessage("Access denied, please check your credentials or"
					+ " register.");
	}

	@Override
	public void onRequestAllNotificationsResult(Collection<Notification> result) {
		activity.processResult(OPERATION_TYPE.FETCH_NOTIFICATIONS,
				OPERATION_RESULT.OK,
				"Notifications fetched.",
				result);
	}

	@Override
	public void onRequestNotificationObjectResult(Quiz result) {
		// TODO Auto-generated method stub

	}


	public USER_TYPE getLoggedInUserAccessAuthority() {
		return loggedInUserAccessAuthority;
	}


	public void setLoggedInUserAccessAuthority(USER_TYPE loggedInUserAccessAuthority) {
		this.loggedInUserAccessAuthority = loggedInUserAccessAuthority;
	}

	@Override
	public void onProfilePictureDownloadRequestResult(Bitmap image) {
		OPERATION_RESULT result = OPERATION_RESULT.ERROR;
		String message = "";
		if(image != null){
			message = "Photo download successful";
			result = OPERATION_RESULT.OK;
		}else{
			message = "Photo download failed!";
		}
		activity.processResult(
				OPERATION_TYPE.PHOTO_DOWNLOAD, result, message, image);
	}

	@Override
	public void onProfileUploadRequestResult(boolean result) {
		OPERATION_RESULT opResult = OPERATION_RESULT.ERROR;
		String message = "";
		if(result){
			message = "Photo download successful";
			opResult = OPERATION_RESULT.OK;
		}else{
			message = "Photo download failed!";
		}
		activity.processResult(
				OPERATION_TYPE.PHOTO_UPLOAD, opResult, message, (Object[])null);
	}
	
	
	
	public UserData getLoggedUser(){
		return mBoundService.getLoggedUser();
	}

}
