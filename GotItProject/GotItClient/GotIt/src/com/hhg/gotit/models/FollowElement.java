package com.hhg.gotit.models;


public class FollowElement {

	private long id;
	
	private UserData userFollowing;

	private UserData userFollowed;
	
	protected FollowElement(UserData follower, UserData victim){
		this.userFollowed = victim;
		this.userFollowing = follower;
	}
	
	public static FollowElement construct(UserData follower, UserData victim){
		return new FollowElement(follower, victim);
	}
	
	public FollowElement(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserData getUserFollowing() {
		return userFollowing;
	}

	public void setUserFollowing(UserData userFollowing) {
		this.userFollowing = userFollowing;
	}

	public UserData getUserFollowed() {
		return userFollowed;
	}

	public void setUserFollowed(UserData userFollowed) {
		this.userFollowed = userFollowed;
	}
	

}
