package edu.hh;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import edu.hh.GotItApi.USER_TYPE;
import edu.hh.auth.User;

public class Utils {
	/**
	 * Helper method to transform USER_TYPE variables into
	 * meaningful (for oauth2) authorities values.
	 */
	static String[] createAuthority(USER_TYPE userType){
		switch(userType){
		case ADMIN:
			return User.PRIVILEGES_ADMIN;
		case FOLLOWER:
			return new String[]{User.PRIVILEGES_FOLLOW};
		case TEEN:
			return new String[]{User.PRIVILEGES_POST_QUIZ, User.PRIVILEGES_FOLLOW};
		default:
			return null;
		}
	}
	

	
	/**
	 * Returns the user_type definition for this user
	 * access configuration.
	 * 
	 * @param auths
	 * @return
	 */
	static USER_TYPE verifyAuthority(String[] auths){
		ArrayList auth = Lists.newArrayList(auths);
		USER_TYPE result = null;
		if(auth.contains(User.PRIVILEGES_POST_QUIZ) 
				&& auth.contains(User.PRIVILEGES_FOLLOW)){
			result = USER_TYPE.TEEN;
		}else if(auth.contains(User.PRIVILEGES_FOLLOW) 
				&& !auth.contains(User.PRIVILEGES_POST_QUIZ)){
			result = USER_TYPE.FOLLOWER;
		}else{
			result = null;
		}
		return result;
	}
}
