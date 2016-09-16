package edu.hh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;
import edu.hh.GotItApi.OP_CODE;
import edu.hh.GotItApi.USER_TYPE;
import edu.hh.datamodel.Notification;
import edu.hh.datamodel.Quiz;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServerTestClass {
	
	private float SUGAR_LEVEL = 1.5f;
	private int DEFAULT_REGISTERED_USERS = 3;
	private String USERNAME_FOLLOWED = "teen2";
	private String USERNAME1 = "teen1";
	private String PASSWORD1 = "pass";
	private String USERNAME2 = "teen2";
	private String PASSWORD2 = "pass";
	private USER_TYPE AUTHORITY = USER_TYPE.TEEN;
	private String CLIENT_ID = "mobile";
	private String TEST_URL = "https://localhost:8443";
	
	private GotItApi gotItSvc_user1 = new SecuredRestBuilder()
	.setLoginEndpoint(TEST_URL + GotItApi.TOKEN_PATH)
	.setUsername(USERNAME1)
	.setPassword(PASSWORD1)
	.setClientId(CLIENT_ID)
	.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
	.setEndpoint(TEST_URL).setLogLevel(LogLevel.NONE).build()
	.create(GotItApi.class);
	
	private GotItApi gotItSvc_user2 = new SecuredRestBuilder()
	.setLoginEndpoint(TEST_URL + GotItApi.TOKEN_PATH)
	.setUsername(USERNAME2)
	.setPassword(PASSWORD2)
	.setClientId(CLIENT_ID)
	.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
	.setEndpoint(TEST_URL).setLogLevel(LogLevel.NONE).build()
	.create(GotItApi.class);
	
	private Quiz q;
	private Collection<Notification> notifs;

	@Test
	public void AtestLogin(){
		//We will retrieve some info of the user..
		USER_TYPE resultAuthority = gotItSvc_user1.getAccessLevelForUser();
		assertEquals(USER_TYPE.TEEN, resultAuthority);
	}

	
	@Test
	public void BtestFollow(){
		OP_CODE op = gotItSvc_user1.followTeen(USERNAME_FOLLOWED);
		assertEquals(OP_CODE.FOLLOW_RELATIONSHIP_CREATED, op);
		op = gotItSvc_user1.followTeen(USERNAME_FOLLOWED);
		assertEquals(OP_CODE.FOLLOW_RELATIONSHIP_ALREADY_EXISTS, op);
		op = gotItSvc_user1.followTeen("sfdfdsfd");
		assertEquals(OP_CODE.FOLLOW_RELATIONSHIP_ERROR, op);
	}
	
	@Test
	public void HtestStopFollow(){
		OP_CODE op = gotItSvc_user1.stopFollowingTeen(USERNAME_FOLLOWED);
		assertEquals(OP_CODE.FOLLOW_RELATIONSHIP_REMOVED, op);
		op = gotItSvc_user1.stopFollowingTeen(USERNAME_FOLLOWED);
		assertEquals(OP_CODE.FOLLOW_RELATIONSHIP_DOES_NOT_EXIST, op);
	}
	
	@Test
	public void CtestFetchTeenList(){
		int numTeens = gotItSvc_user1.getTeenList().size();
		assertEquals(DEFAULT_REGISTERED_USERS, numTeens);
	}
	
	/**
	 * Will request a new quiz, check if it's valid by trying to compare
	 * this test username to the one stored in the quiz, and reply
	 * some or all of it's questions after checking for types.
	 * 
	 */
	@Test
	public void DtestRequestQuiz(){
		q = gotItSvc_user2.requestNewQuiz();
		assertEquals(false, q == null);
		
		int i = 0;
		for(String question : q.getQuestions()){
			switch(q.getReplyDataTypes().get(i)){
			case FLOAT:
				q.getReplies().set(i, "0.5");
				break;
			case STRING:
				q.getReplies().set(i, "TEST");
				break;
			}
			i++;
		}
	}
	
	@Test
	public void EtestSendRepliedQuiz(){
		q = gotItSvc_user2.requestNewQuiz();
		
		System.err.println("We received a quiz with "+q.getQuestions().size()+" questions, ");
		
		assertEquals(false, q == null);
		
		int i = 0;
		for(String question : q.getQuestions()){
			switch(q.getReplyDataTypes().get(i)){
			case FLOAT:
				q.getReplies().set(i, String.valueOf(SUGAR_LEVEL));
				break;
			case STRING:
				q.getReplies().set(i, "TEST");
				break;
			}
			i++;
		}
		
		System.err.println("Uploading test with "
				+q.getReplies().size()+" replied questions.");
		gotItSvc_user2.uploadCompletedQuiz(q);
		
	}
	
	@Test
	public void FcheckPendingNotifications(){
		notifs = gotItSvc_user1.getPendingNotifications();
		assertEquals(true, notifs.size() > 0);
	}
	
	@Test
	public void GretrievePendingNotification(){
		notifs = gotItSvc_user1.getPendingNotifications();
		Iterator<Notification> it = notifs.iterator();
		Notification n = it.next();
		Quiz q = gotItSvc_user1.getNotificationObject(n.getId());
		assertEquals(true, q != null);
		assertEquals(true, q.getDateTaken() != null);
		assertEquals(true, q.getAuthor().getUsername().equals(USERNAME2));
		assertEquals(true, q.getQuestions().size() > 0);
		System.out.println("Retrieved notification with quiz: ");
		
		int i = 0;
		for(String question : q.getQuestions()){
			System.out.println(question + " " + q.getReplies().get(i));
		}
	}
	
	@Test
	public void ItestChangeFollowableStatus(){
		boolean newStatus = gotItSvc_user2.setFollowableStatus(false);
		assertEquals(false, newStatus);
		
		OP_CODE op = gotItSvc_user1.followTeen(USERNAME_FOLLOWED);
		assertEquals(OP_CODE.FOLLOW_RELATIONSHIP_NOT_ALLOWED, op);
	}
	
	@Test
	public void JregisterNewUser(){
		RestAdapter restAdapter = new RestAdapter.Builder()
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setEndpoint(TEST_URL)
        .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
        .build();
		
		GotItApi api = restAdapter.create(GotItApi.class);

		boolean result = api.signUp("newUser1", "pass2", "User ", " new", 1001,
				true, USER_TYPE.TEEN);
		assertEquals(true, result);
		
		GotItApi gotItSvc_newUser = new SecuredRestBuilder()
		.setLoginEndpoint(TEST_URL + GotItApi.TOKEN_PATH)
		.setUsername("newUser1")
		.setPassword("pass2")
		.setClientId(CLIENT_ID)
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.NONE).build()
		.create(GotItApi.class);
		USER_TYPE resultAuthority = gotItSvc_user1.getAccessLevelForUser();
		assertEquals(AUTHORITY, resultAuthority);
	}
	
	@Test
	public void KuploadAndDownloadImage(){
		File file = new File("C:\\Users\\Harold\\Pictures\\wallpapers\\the_fall_by_exphrasis-d87aj5y.jpg");
		TypedFile tf = new TypedFile("image/jpg", file);
		assertTrue(gotItSvc_user1.uploadPicture(tf));
		Response imageReturned = gotItSvc_user1.getProfilePicture(USERNAME1);
		TypedInput inputStream = imageReturned.getBody();
		assertTrue(inputStream != null);
		
	}
}
