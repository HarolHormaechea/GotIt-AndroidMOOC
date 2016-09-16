package edu.hh;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import retrofit.mime.TypedFile;
import edu.hh.GotItApi.OP_CODE;
import edu.hh.GotItApi.USER_TYPE;
import edu.hh.auth.User;
import edu.hh.datamodel.FollowElement;
import edu.hh.datamodel.Notification;
import edu.hh.datamodel.Quiz;
import edu.hh.datamodel.Quiz.QUIZ_TYPE;
import edu.hh.datamodel.Quiz.TimeOfDay;
import edu.hh.datamodel.UserData;
import edu.hh.repositories.ImageSave;



/**
 * Controller for all rest end points. 
 * 
 * Data access through JPA is managed by a PersistentDataManager
 * object autowired by Spring, and managed through a @Component
 * annotation, to better separate logic and data.
 * 
 * This way, we would not require any change to this class if we
 * decided, for example, to use a Postgres database to store
 * everything. Or another server altogether.
 * 
 * @author Harold
 *
 */
@Controller
public class RestController {
	
	@Autowired
	private PersistentDataManager dataManager;
	
	private ImageSave imageManager = new ImageSave();
	
	
	/**
	 * We will create some default users so the testers can properly assess
	 * the server without messing around with user creation each time.
	 */
	@PostConstruct
	private void initDefaultUsers(){
		System.out.println("Post Construct: Initializing default users.");
		dataManager.createNewUser("teen1", "pass", "Teen ", "1 ", 100, true, GotItApi.USER_TYPE.TEEN);
		dataManager.createNewUser("teen2", "pass", "Teen ", "2 ", 200, true, GotItApi.USER_TYPE.TEEN);
		dataManager.createNewUser("follower1", "pass", "Follower ", "1 ", 0, true,  GotItApi.USER_TYPE.FOLLOWER);
		System.out.println("Post Construct: default users have been added to authentication list.");
		System.out.println("Post Construct: Number of registered non-admin users: "+dataManager.getUserCount());
	}
	
	
	
	
	/**
	 * Conveniency method to check we have actually logged in properly...
	 * 
	 * @param principal
	 * @return
	 */
	@RequestMapping(value=GotItApi.REQ_USER_ACCESS_LEVEL, method=RequestMethod.GET)
	public @ResponseBody USER_TYPE getUserAccessLevel(Principal principal){
		return Utils.verifyAuthority(dataManager.getUserPrivileges(principal));
	}
	
	
	/**
	 * Adds this principal to the teen's followers list.
	 * 
	 * @return
	 */
	@RequestMapping(value=GotItApi.TEEN_FOLLOW, method=RequestMethod.POST)
	@PreAuthorize("hasRole('"+User.PRIVILEGES_FOLLOW+"')")
	public @ResponseBody OP_CODE follow(
			@PathVariable(GotItApi.USERNAME_PARAM) String teen, Principal principal){
		
		OP_CODE requestResult = OP_CODE.FOLLOW_RELATIONSHIP_ERROR;	//Default initialization at error mode.
		//First, we verify the existence of the requested teen to be followed
		UserData teenUser = dataManager.getUserData(teen);
		//and the proper existence of the user requesting to follow him.
		UserData followerUser = dataManager.getUserData(principal.getName());
		
		if(teenUser == null || followerUser == null){
			System.out.println("Target Teen exists? NO");
			requestResult = OP_CODE.FOLLOW_RELATIONSHIP_ERROR;
		}else if(!teenUser.getAllowsFollowers()){
			System.out.println("Attempted to follow a user who does not allow such action.");
			requestResult = OP_CODE.FOLLOW_RELATIONSHIP_NOT_ALLOWED;
		}
		else{
			System.out.println("Target Teen exists? YES");

			//Second, we check the existence of a previous follow relationship
			//between these two so we can know what to do next.
			FollowElement result = dataManager.
					getFollowElementByUserFollowedAndUserFollowing(teenUser, followerUser);
			
			//IF this relationship does not exist and the teen exists,
			//we add it.
			if(result == null){
				dataManager.follow(followerUser, teenUser);
				
				System.out.println("New follower ("+followerUser.getUsername()+
						") following ("+teenUser.getUsername()+")");
				requestResult = OP_CODE.FOLLOW_RELATIONSHIP_CREATED;
			
			}else{
				requestResult = OP_CODE.FOLLOW_RELATIONSHIP_ALREADY_EXISTS;
			}
		}
		System.out.println("We currently have "+dataManager.getFollowRelationshipsCount()+" following "
				+ "relationships stored in database.");
		return requestResult;
	}
	
	/**
	 * Stops the follower-followed relationship between a teen and the
	 * requesting follower.
	 * 
	 * @param teen
	 * @param p
	 * @return
	 */
	@RequestMapping(value=GotItApi.TEEN_STOP_FOLLOW, method=RequestMethod.POST)
	@PreAuthorize("hasRole('"+User.PRIVILEGES_FOLLOW+"')")
	public @ResponseBody OP_CODE stopFollowing(
			@PathVariable(GotItApi.USERNAME_PARAM) String teen, Principal principal){
		
		OP_CODE requestResult = OP_CODE.FOLLOW_RELATIONSHIP_ERROR;	//Default initialization at error mode.
		System.out.println(principal.getName()+
				" is attempting to stop following "+teen);
		//First, we verify the existence of the requested teen to be followed
		UserData teenUser = dataManager.getUserData(teen);
		//and the proper existence of the user requesting to follow him.
		UserData followerUser = dataManager.getUserData(principal.getName());
				
		
		//We will search for the relationship entry right away. 
		//we check the existence of a previous follow relationship
		//between these two so we can know what to do next.
		FollowElement result = dataManager.
				getFollowElementByUserFollowedAndUserFollowing(teenUser, followerUser);
		
		//IF this relationship does not exist and the teen exists,
		//we add it.
		if(result != null){
			dataManager.deleteFollowRelationship(result);
			requestResult = OP_CODE.FOLLOW_RELATIONSHIP_REMOVED;
			System.out.println("A following relationship has been removed.");
		
		}else{
			requestResult = OP_CODE.FOLLOW_RELATIONSHIP_DOES_NOT_EXIST;
			System.out.println("A following relationship removal has failed by "+principal.getName()+".");
		}
		
		return requestResult;
	}
	
	
	/**
	 * Returns a the full collection of teens stored in the server.
	 * 
	 * @return
	 */
	@RequestMapping(value=GotItApi.TEEN_FULL_LIST, method=RequestMethod.GET)
	@PreAuthorize("hasRole('"+User.PRIVILEGES_FOLLOW+"')")
	public @ResponseBody Collection<UserData> fetchTeenList(){
		return dataManager.getAllUsersData();
	}
	
	@RequestMapping(value = GotItApi.TEEN_IMAGE, method = RequestMethod.GET)
	public void getImage(
			@PathVariable(GotItApi.USERNAME_PARAM) String teen,
			HttpServletResponse reply) {
	    try {
	    	TypedFile file = imageManager.copyImageData(teen);
	        if(file != null && file.file() != null){
	        	reply.setStatus(200);
	        	file.writeTo(reply.getOutputStream());
	        }else{
	        	reply.setStatus(404);
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	@RequestMapping(value = GotItApi.TEEN_PUT_LOGGED_USER_PICTURE,method = RequestMethod.POST)
	public @ResponseBody boolean storeImage(Principal p, @RequestPart(GotItApi.IMAGE_PARAM) MultipartFile image) {
        try {
			imageManager.saveImageData(p.getName(), image.getInputStream());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Creates and sends the client a new Quiz.
	 */
	@RequestMapping(value=GotItApi.TEEN_REQ_QUIZ, method=RequestMethod.GET)
	@PreAuthorize("hasRole('"+User.PRIVILEGES_POST_QUIZ+"')") //Does not work??
	public @ResponseBody Quiz requestNewQuiz(Principal principal){
		switch(Utils.verifyAuthority(dataManager.getUserPrivileges(principal))){
		case TEEN:
			Calendar c = Calendar.getInstance();
			TimeOfDay timeForQuiz;
			int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
			if(timeOfDay > 6 && timeOfDay < 19)
				timeForQuiz = TimeOfDay.MEAL_TIME;
			else
				timeForQuiz = TimeOfDay.BED_TIME;
			
			return new Quiz(QUIZ_TYPE.DEFAULT, timeForQuiz);
		default:
			return null;
		
		}
		
	}
	
	
	/**
	 * Retrieves a Quiz and stores it in the quiz database for future
	 * analysis or retrieval.
	 * 
	 */
	@RequestMapping(value = GotItApi.TEEN_UPLOAD_QUIZ, method=RequestMethod.POST)
	@PreAuthorize("hasRole('"+User.PRIVILEGES_POST_QUIZ+"')") //Does not work?
	public @ResponseBody boolean postNewQuiz(@RequestBody Quiz q, Principal principal){
		switch(Utils.verifyAuthority(dataManager.getUserPrivileges(principal))){
		case TEEN:
			boolean result = true;
			System.out.println("New request to store a completed quiz by "+principal.getName());
			q = Quiz.packageQuiz(q, new Date(), dataManager.getUserData(principal.getName()));
			
			dataManager.storeQuiz(q);
			System.out.println("Quiz stored for teen "+q.getAuthor().getFirstName()+" "
					+ q.getAuthor().getLastName() 
					+ "("+q.getAuthor().getUsername()+")"
					+ " at "+q.getDateTaken().toString());
			if(dataManager.getUserData(principal.getName()).getAllowsFollowers())
				notifyFollowers(q);
			return result;
		default:
			return false;
		}
		
	}
	
	
	/**
	 * Gives the client a list of notifications 
	 * @param p
	 * @return
	 */
	@RequestMapping(value=GotItApi.FOLLOWER_REQ_PENDING_NOTIFICATIONS, method=RequestMethod.GET)
	@PreAuthorize("hasRole('"+User.PRIVILEGES_FOLLOW+"')")
	public @ResponseBody Collection<Notification> fetchPendingNotifications(Principal principal){
		UserData followerUser = dataManager.getUserData(principal.getName());
		return dataManager.retrieveNotificationsByUser(followerUser);
	}
	
	
	
	
	
	/**
	 * Helper method to create new notifications for everyone after
	 * a teen creates a quiz.
	 */
	private void notifyFollowers(Quiz quiz){
		
		Collection<FollowElement> followers = dataManager
				.getFollowElementByUserFollowed(quiz.getAuthor());
		System.out.println("Creating notifications for "+followers.size()+" followers.");
		
		Iterator<FollowElement> it = followers.iterator();
		while(it.hasNext()){
			FollowElement follow = it.next();
			dataManager.saveNewNotification(
					Notification.notificationBuilder(follow.getUserFollowing(), quiz));
		}
		
		System.out.println("We have "+dataManager.getPendingNotificationsCount()+
				" notifications pending delivery in database.");
	}
	
	
	/**
	 * Retrieves a single notification target (a quiz, in this case) by its
	 * object-created unique ID.
	 * 
	 * @param id
	 * @param reply
	 * @return
	 */
	@RequestMapping(value=GotItApi.FOLLOWER_REQ_PENDING_NOTIFICATION_BY_ID, method=RequestMethod.GET)
	@PreAuthorize("hasRole('"+User.PRIVILEGES_FOLLOW+"')")
	public @ResponseBody Quiz fetchNotificationElementById(
			@PathVariable(GotItApi.NOTIF_ID_PARAM) int id, HttpServletResponse reply,
			Principal p){
		Quiz q = null;
		q = dataManager.getQuiz(id);
		if(q == null){
			try {
				reply.sendError(404, "No matching notifications found in server.");
				System.err.println("User "+p.getName()+" attempted to fetch a non existing quiz.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(q.getQuestions() == null || q.getQuestions().size() < 1){
			try {
				reply.sendError(404, "No existing quiz found.");
				System.err.println("User "+p.getName()+" attempted to fetch an empty quiz.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return q;
	}
	
	/**
	 * Retrieves all the quizzes for this user so he can show
	 * their data in any way he wishes.
	 */
	@RequestMapping(value=GotItApi.TEEN_REQ_ALL_OWN_QUIZZES, method=RequestMethod.GET)
	public @ResponseBody Collection<Quiz> retrieveAllQuizzes(Principal p){
		return dataManager.getAllQuizByAuthor(dataManager.getUserData(p.getName()));
	}
	
	/**
	 * Changes the followable status of the requesting user. If the user
	 * requests to not be "followable", we will remove any follow relationships
	 * from our databases, so nobody will receive his updates anymore.
	 * 
	 * This will NOT delete previous notifications, only avoid creating new ones.
	 * 
	 */
	@RequestMapping(value=GotItApi.CHANGE_FOLLOWABLE_ATTRIBUTE, method=RequestMethod.POST)
	public @ResponseBody boolean changeFollowableStatus(Principal p, 
			@PathVariable(GotItApi.ALLOWS_BEING_FOLLOWED_PARAM) boolean allowFollowers){
		UserData resultData = dataManager
				.setFollowable(dataManager.getUserData(p.getName()), allowFollowers);
		if(!allowFollowers){
			Collection<FollowElement> fe = dataManager
					.getFollowElementByUserFollowed(resultData);
			
			for(FollowElement element : fe){
				dataManager.deleteFollowRelationship(element);
			}
		}
		return resultData.getAllowsFollowers();
	}
	
	
	/**
	 * Allows an user to retrieve his or her own data.
	 * 
	 * @param p
	 * @return
	 */
	@RequestMapping(value=GotItApi.TEEN_GET_LOGGED_USER_DATA, method=RequestMethod.GET)
	public @ResponseBody UserData getLoggedUserData(Principal p){
		UserData userData = dataManager.getUserData(p.getName()); 
		System.out.println("User checking for own data of type "+userData.getUserType());
		return userData;
	}

	
	/**
	 * Allows a new user to sign up
	 */
	@RequestMapping(value=GotItApi.SIGN_UP, method=RequestMethod.POST)
	public @ResponseBody boolean signUp(
			@PathVariable(GotItApi.USERNAME_PARAM) String username,
			@PathVariable(GotItApi.PASSWORD_PARAM) String pwd,
			@PathVariable(GotItApi.FIRSTNAME_PARAM) String firstName,
			@PathVariable(GotItApi.LASTNAME_PARAM) String lastName,
			@PathVariable(GotItApi.MEDICAL_RECORD_PARAM) Integer medicalRecordNumber,
			@PathVariable(GotItApi.ALLOWS_BEING_FOLLOWED_PARAM) boolean allowsFollowers,
			@PathVariable(GotItApi.AUTHORITIES_PARAM) USER_TYPE userType)
	{
		if(!validateUserCreationAttempt(username, pwd, firstName, lastName, medicalRecordNumber)){
			return false;
		}else
		{
			return dataManager.createNewUser(username, pwd, firstName,
					lastName, medicalRecordNumber, allowsFollowers, userType);
		}
	}
	
	
	/**
	 * Helper method to condense the validation of new users basic data.
	 * 
	 * @param user
	 * @param pwd
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	private boolean validateUserCreationAttempt(String username, String pwd,
			String firstName, String lastName, Integer medicalRecordNumber) {
		return  User.validatePwd(pwd) 
				&& User.validateUsername(username) 
				&& UserData.validateName(firstName)
				&& UserData.validateName(lastName)
				&& medicalRecordNumber != null || medicalRecordNumber > 0;
	}


}
