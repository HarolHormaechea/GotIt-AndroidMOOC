/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package edu.hh.auth;

import java.util.Collection;
import java.util.Collections;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class User implements UserDetails {
	
	public static final String PRIVILEGES_FOLLOW = "FOLLOW";
	public static final String PRIVILEGES_POST_QUIZ = "POST_QUIZ";
	public static final String[] PRIVILEGES_ADMIN = new String[]{"ADMIN", PRIVILEGES_FOLLOW, PRIVILEGES_POST_QUIZ};
	
	public static UserDetails create(String username, String password,
			String...authorities) {
		return new User(username, password, authorities);
	}
	
	public User(){}

	@ElementCollection
	private Collection<GrantedAuthority> authorities;
	private String password;
	
	@Id
	private String username;

	@SuppressWarnings("unchecked")
	private User(String username, String password) {
		this(username, password, Collections.EMPTY_LIST);
	}

	private User(String username, String password,
			String...authorities) {
		this.username = username;
		this.password = password;
		this.authorities = AuthorityUtils.createAuthorityList(authorities);
	}

	private User(String username, String password,
			Collection<GrantedAuthority> authorities) {
		super();
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public static boolean validateUsername(String username){
		return (username.length() > 4 && username.length() < 10);
	}
	
	public static boolean validatePwd(String username){
		return username != null && (username.length() > 4 && username.length() < 10);
	}

}
