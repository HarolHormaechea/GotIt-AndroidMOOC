package com.hhg.gotit.models;

import java.util.Collection;


import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

public interface GotItApi {
	public enum OP_CODE{FOLLOW_RELATIONSHIP_ALREADY_EXISTS,FOLLOW_RELATIONSHIP_DOES_NOT_EXIST,
		FOLLOW_RELATIONSHIP_CREATED, FOLLOW_RELATIONSHIP_REMOVED, FOLLOW_RELATIONSHIP_ERROR,
		FOLLOW_RELATIONSHIP_NOT_ALLOWED}
	
	public static enum USER_TYPE{TEEN, FOLLOWER, ADMIN}

	
	public static final String TOKEN_PATH = "/oauth/token";
	public static final String USERNAME_PARAM = "teen";
	public static final String USERNAME_PATH = "{teen}";
	public static final String PASSWORD_PARAM = "pwd";
	public static final String PASSWORD_PATH = "{pwd}";
	public static final String FIRSTNAME_PARAM = "fname";
	public static final String FIRSTNAME_PATH = "{fname}";
	public static final String LASTNAME_PARAM = "lname";
	public static final String LASTNAME_PATH = "{lname}";
	public static final String MEDICAL_RECORD_PARAM = "record";
	public static final String MEDICAL_RECORD_PATH = "{record}";
	public static final String AUTHORITIES_PARAM = "auth";
	public static final String AUTHORITIES_PATH = "{auth}";
	
	public static final String ALLOWS_BEING_FOLLOWED_PARAM = "followable";
	public static final String ALLOWS_BEING_FOLLOWED_PATH= "{followable}";
	
	public static final String NOTIF_ID_PARAM = "notification_id";
	public static final String IMAGE_PARAM = "image_param";
	
	public static final String TEEN_USERNAME_PATH = "{teen}";
	public static final String NOTIF_ID_PATH = "{notification_id}";
	
	//The path for user access level check
	public static final String REQ_USER_ACCESS_LEVEL = "/teen/privileges";
	
	//The path where "Teen" service will reside
	public static final String TEEN_SVC_PATH = "/teen";
	
	//get data from logged in user
	public static final String TEEN_GET_LOGGED_USER_DATA = "/teen/me";
	public static final String TEEN_PUT_LOGGED_USER_PICTURE = "/teen/me/add_picture";
	
	//The path to list all the teens
	public static final String TEEN_FULL_LIST = TEEN_SVC_PATH+"/list_all";
	
	//Path to get the profile image of a teen
	public static final String TEEN_IMAGE = TEEN_SVC_PATH+"/"+TEEN_USERNAME_PATH+"/picture";
	
	//The path to follow a teen
	public static final String TEEN_FOLLOW = TEEN_SVC_PATH+"/"+TEEN_USERNAME_PATH+"/follow";
	
	//The path to stop following a teen
	public static final String TEEN_STOP_FOLLOW = TEEN_SVC_PATH+"/"+TEEN_USERNAME_PATH+"/stop_follow";
	
	
	public static final String CHANGE_FOLLOWABLE_ATTRIBUTE = TEEN_SVC_PATH+
			"/set_followable/"+ALLOWS_BEING_FOLLOWED_PATH;
	
	//The path for a follower to check for pending notifications
	//TODO: Implementation self-hint.. why not make this return a list with notification id's
	//so the user does not have to load ALL but only pre-load key data and then ask for
	//the whole notification details on click, for example? This last thing would make that
	//notification "read".
	public static final String FOLLOWER_REQ_PENDING_NOTIFICATIONS =
			TEEN_SVC_PATH + "/pending_notifications";
	
	//The path to retrieve a pending (or not) notification by it's id.
	public static final String FOLLOWER_REQ_PENDING_NOTIFICATION_BY_ID=
			FOLLOWER_REQ_PENDING_NOTIFICATIONS + "/"+NOTIF_ID_PATH;
	
	//The path to request a new empty quiz for a Teen.
	public static final String TEEN_REQ_QUIZ = TEEN_SVC_PATH + "/quiz/request_new";
	//The path to request a new empty quiz for a Teen.
	public static final String TEEN_UPLOAD_QUIZ = TEEN_SVC_PATH + "/quiz/upload";
	
	public static final String TEEN_REQ_ALL_OWN_QUIZZES = TEEN_SVC_PATH + "/quiz/retrieve_all";
	
	public static final String SIGN_UP = TEEN_SVC_PATH + "/sign_up/"+USERNAME_PATH
			+"/"+PASSWORD_PATH+"/"+FIRSTNAME_PATH+"/"+LASTNAME_PATH+"/"+
			MEDICAL_RECORD_PATH+"/"+AUTHORITIES_PATH+"/"+ALLOWS_BEING_FOLLOWED_PATH;
	
	@POST(SIGN_UP)
	public boolean signUp(@Path(USERNAME_PARAM) String username,
			@Path(PASSWORD_PARAM) String pwd,
			@Path(FIRSTNAME_PARAM) String firstName,
			@Path(LASTNAME_PARAM) String lastName,
			@Path(MEDICAL_RECORD_PARAM) Integer medicalRecordNumber,
			@Path(ALLOWS_BEING_FOLLOWED_PARAM) boolean allowsFollowers,
			@Path(AUTHORITIES_PARAM) USER_TYPE userType);
	
	@GET(REQ_USER_ACCESS_LEVEL)
	public USER_TYPE getAccessLevelForUser();
	
	@GET(TEEN_FULL_LIST)
	public Collection<UserData> getTeenList();
	
	@GET(TEEN_GET_LOGGED_USER_DATA)
	public UserData getMyData();
	
	@POST(TEEN_FOLLOW)
	public GotItApi.OP_CODE followTeen(@Path(USERNAME_PARAM) String id);
	
	@POST(TEEN_STOP_FOLLOW)
	public GotItApi.OP_CODE stopFollowingTeen(@Path(USERNAME_PARAM) String id);
	
	@GET(FOLLOWER_REQ_PENDING_NOTIFICATIONS)
	public Collection<Notification> getPendingNotifications();
	
	@GET(FOLLOWER_REQ_PENDING_NOTIFICATION_BY_ID)
	public Quiz getNotificationObject(@Path(NOTIF_ID_PARAM) int id);
	
	@GET(TEEN_REQ_QUIZ)
	public Quiz requestNewQuiz();
	
	@GET(TEEN_REQ_ALL_OWN_QUIZZES)
	public Collection<Quiz> getAllQuizzes();
	
	@POST(TEEN_UPLOAD_QUIZ)
	public boolean uploadCompletedQuiz(@Body Quiz quiz);
	
	@POST(CHANGE_FOLLOWABLE_ATTRIBUTE)
	public boolean setFollowableStatus(@Path(ALLOWS_BEING_FOLLOWED_PARAM) boolean followable);
	
	@Multipart
	@POST(TEEN_PUT_LOGGED_USER_PICTURE)
	public boolean uploadPicture(@Part(IMAGE_PARAM) TypedFile image);
	
	@GET(TEEN_IMAGE)
	public Response getProfilePicture(@Path(USERNAME_PARAM) String username);
}
