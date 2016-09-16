package edu.hh;

import java.security.Principal;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import edu.hh.GotItApi.USER_TYPE;
import edu.hh.auth.ClientAndUserDetailsService;
import edu.hh.auth.User;
import edu.hh.datamodel.FollowElement;
import edu.hh.datamodel.Notification;
import edu.hh.datamodel.Quiz;
import edu.hh.datamodel.UserData;
import edu.hh.repositories.FollowStatusRepository;
import edu.hh.repositories.PendingNotificationsRepository;
import edu.hh.repositories.QuizRepository;
import edu.hh.repositories.UserDataRepository;
import edu.hh.repositories.UsersRepository;

/**
 * Manager class using wrapper façade pattern to hide the details on
 * the different repositories used by the server through simple
 * method calls.
 * 
 * This allows us to change the underlying persistence mechanism
 * without changing the main logic classes. For example, we would
 * only have to change this class to swap the current JPA-based
 * persistence modules with SQL-based ones.
 * 
 * @author Harold
 *
 */
@Component
public class PersistentDataManager {
	@Autowired
	private UsersRepository userLoginRepository;
	
	@Autowired
	private UserDataRepository userDataRepository;
	
	@Autowired
	private FollowStatusRepository followStatusRepository;
	
	@Autowired
	private QuizRepository quizRepository;
	
	@Autowired
	private PendingNotificationsRepository notificationsRepository;
	
	@Autowired
	@Qualifier("userDetailsService")
	private ClientAndUserDetailsService logInService;
	
	
	
	public PersistentDataManager(){}
	
	/**
	 * We will create some default users so the testers can properly assess
	 * the server without messing around with user creation each time.
	 */
	public void initDefaultUsers(){
		System.out.println("Post Construct: Initializing default users.");
		createNewUser(
				"teen1", "pass", "Teen ", "1", 100, true, GotItApi.USER_TYPE.TEEN);
		
		createNewUser(
				"teen2", "pass", "Teen ", "2", 200, true, GotItApi.USER_TYPE.TEEN);
		
		createNewUser(
				"follower1", "pass", "Follower ", "1", -1, true, GotItApi.USER_TYPE.FOLLOWER);
		
		try{
			System.out.println("Post Construct: Default users have been added to authentication list.");
		}catch(UsernameNotFoundException ex){
			System.err.println("Users are not being properly authorized for remote access.");
		}
	}
	
	/**
	 * Creates a new user, assigns it a new UserData object, and defines
	 * it's attributes. It will check for duplicates, and if found,
	 * will return false to avoid a fail.
	 * 
	 */
	public boolean createNewUser(
			
			String user, String password, String firstName,
			String lastName, int medicalRecord, boolean allowsFollowers, GotItApi.USER_TYPE privileges){
		
		
		if(!userLoginRepository.findByUsername(user).isEmpty()){
			return false;
		}
		
		UserDetails teen1 = User.create(user, password, Utils.createAuthority(privileges));
		userLoginRepository.save((User)teen1);
		
		UserData teen_1 = new UserData();
		teen_1.setFirstName(firstName);
		teen_1.setLastName(lastName);
		teen_1.setUsername(user);
		teen_1.setAllowsFollowers(allowsFollowers);
		teen_1.setMedicalRecordNumber(medicalRecord);
		
		
		userDataRepository.save(teen_1);
		logInService.addUser(teen1);
		
		return true;
	}
	
	
	
	/**
	 * Returns the privileges of a given user by it's username.
	 * @return 
	 */
	public String[] getUserPrivileges(Principal principal){
		String result[];
		
		org.springframework.security.core.userdetails.User activeUser = 
				(org.springframework.security.core.userdetails.User) ((Authentication) principal).getPrincipal();
		
		Collection<GrantedAuthority> authorities = activeUser.getAuthorities();
		result = new String[authorities.size()];
		
		int pos = 0;
		for(GrantedAuthority auth : authorities){
			result[pos] = auth.getAuthority();
			pos++;
		}
		
		return result;
	}
	
	/**
	 * Returns the UserData associated with a user.
	 * @return 
	 */
	public UserData getUserData(String username){
		return userDataRepository.findFirstByUsername(username);
	}
	
	/**
	 * Sets the followable status of an user.
	 */
	public UserData setFollowable(UserData user, boolean allowsFollowers){
		user.setAllowsFollowers(allowsFollowers);
		return userDataRepository.save(user);
	}
	
	/**
	 * Returns a list of all the UserData objects stored.
	 */
	public Collection<UserData> getAllUsersData(){
		
		return Lists.newArrayList(userDataRepository.findAll());
	}
	
	/**
	 * Finds the FollowElement which links two users (if
	 * it exists)
	 * 
	 * @param followerUser 
	 * @param teenUser 
	 * @return 
	 */
	public FollowElement getFollowElementByUserFollowedAndUserFollowing(
			UserData userFollowed, UserData followerUser){
		return followStatusRepository
		.findFirstByUserFollowedAndUserFollowing(
				userFollowed, followerUser);
	}
	
	/**
	 * Returns a collection of all the users (UserData objects) which are
	 * registered to follow another UserData.
	 * 
	 * @param userFollowed
	 * @return
	 */
	public Collection<FollowElement> getFollowElementByUserFollowed(UserData userFollowed){
		return followStatusRepository.findByUserFollowed(userFollowed);
	}
	
	
	/**
	 * Deletes the specified followed-follower relationship
	 * from the underlying database.
	 * 
	 * @param element
	 */
	public void deleteFollowRelationship(FollowElement element){
		followStatusRepository.delete(element);
	}
	
	
	
	/**
	 * Stores a new notification pending to be read by clients.
	 * @param notification 
	 */
	public void saveNewNotification(Notification notification){
		notificationsRepository.save(notification);
	}
	
	/**
	 * Retrieves all the notifications available for the user and
	 * not yet read.
	 * @return 
	 */
	public Collection<Notification> retrieveNotificationsByUser(UserData user){
		return notificationsRepository.findByNotificationTarget(user);
	}
	
	
	/**
	 * Stores a new quiz in the underlying database.
	 */
	public Quiz storeQuiz(Quiz quiz){
		return quizRepository.save(quiz);
	}
	
	/**
	 * Retrieves the quiz with the given ID.
	 */
	public Quiz getQuiz(int id){
		return quizRepository.findFirstById(id);
	}
	
	public Collection<Quiz> getAllQuizByAuthor(UserData author){
		return quizRepository.findByAuthor(author);
	}
	
	/**
	 * Stores a new follow relationship.
	 * 
	 * @param followerUser 
	 * @param teenUser 
	 */
	public FollowElement follow(UserData followerUser, UserData teenUser){
		return followStatusRepository.save(
				FollowElement.construct(followerUser, teenUser));
	}
	
	/**
	 * Removes a new follow relationship.
	 */
	public void stopFollow(){}
	
	/**
	 * Retrieves the count of follow relationships existing in the database
	 */
	public long getFollowRelationshipsCount(){
		return followStatusRepository.count();
	}
	
	/**
	 * Retrieves the count of notifications existing in the database
	 * and pending delivery.
	 * 
	 *TODO: pending delivery! filter results!
	 */
	public long getPendingNotificationsCount(){
		return notificationsRepository.count();
	}
	
	/**
	 * Retrieves the count of registered users in our app.
	 */
	public long getUserCount(){
		return userDataRepository.count();
	}
}
