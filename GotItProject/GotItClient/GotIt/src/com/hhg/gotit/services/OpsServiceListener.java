package com.hhg.gotit.services;

import java.util.Collection;

import retrofit.mime.TypedFile;
import android.graphics.Bitmap;

import com.hhg.gotit.models.GotItApi.OP_CODE;
import com.hhg.gotit.models.GotItApi.USER_TYPE;
import com.hhg.gotit.models.Notification;
import com.hhg.gotit.models.Quiz;
import com.hhg.gotit.models.UserData;

public interface OpsServiceListener {
	public void onRegisterNewUserResult(boolean result);
	public void onLoginResult(USER_TYPE userType);
	public void onCurrentUserDataRequestResult(UserData data);
	public void onFollowAttemptResult(OP_CODE result);
	public void onStopFollowAttemptResult(OP_CODE result);
	public void onFollowableUserListRequestResult(Collection<UserData> userData);
	public void onFollowableSelfStatusChange(boolean updatedStatus);
	public void onNewQuizReceived(Quiz q);
	public void onUploadAttemptResult(boolean status);
	public void onUnexpectedError(String errorCause);
	public void onRequestAllNotificationsResult(Collection<Notification> result);
	public void onRequestNotificationObjectResult(Quiz result);
	public void onProfilePictureDownloadRequestResult(Bitmap image);
	public void onProfileUploadRequestResult(boolean result);
}
