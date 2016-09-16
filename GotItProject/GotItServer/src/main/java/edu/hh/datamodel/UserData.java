package edu.hh.datamodel;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import edu.hh.GotItApi.USER_TYPE;

/**
 * Teen class designed to define any teen with it's basic data. It will be "linked"
 * to the authentication usernames by the key "userName".  
 **/
@Entity
public class UserData {
	private long id;
	
	/*IMPORTANT: For security reasons (this object will be sent to 
	 * any client if it requests it) this parameter will not be a direct reference
	 * to the UserDetails element which allows this teen to log in.
	 * This increases safety through the cost of referential integrity
	 * and manual management of access list / teen object list synchronization. */
	@Id
	private String username;
	
	@Enumerated
	private USER_TYPE userType;
	
	private String firstName, lastName;
	
	private boolean allowsFollowers;
	
	private int medicalRecordNumber;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getMedicalRecordNumber() {
		return medicalRecordNumber;
	}

	public void setMedicalRecordNumber(int medicalRecordNumber) {
		this.medicalRecordNumber = medicalRecordNumber;
	}


	public USER_TYPE getUserType() {
		return userType;
	}

	public void setUserType(USER_TYPE userType) {
		this.userType = userType;
	}

	public static boolean validateName(String name){
		return name.length() > 2 && name.length() < 30;
	}

	public boolean getAllowsFollowers() {
		return allowsFollowers;
	}

	public void setAllowsFollowers(boolean allowsFollowers) {
		this.allowsFollowers = allowsFollowers;
	}
	
	
}

