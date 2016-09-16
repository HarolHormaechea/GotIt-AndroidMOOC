/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package edu.hh.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.provisioning.UserDetailsManager;


/**
 * A class that combines a UserDetailsService and ClientDetailsService
 * into a single object.
 * 
 * @author jules
 *
 */
public class ClientAndUserDetailsService implements UserDetailsService,
		ClientDetailsService {

	private final ClientDetailsService clients_;

	private final UserDetailsManager users_;
	
	private final ClientDetailsUserDetailsService clientDetailsWrapper_;
	
	/**
	 * Adds a new server to this service.
	 * 
	 * @param clients
	 * @param users
	 */
	public void addUser(String username, String password, String[] authorities){
		users_.createUser(User.create(username, password, authorities));
	}
	
	/**
	 * Adds a new server to this service.
	 * 
	 * @param clients
	 * @param users
	 */
	public void addUser(UserDetails user){
		users_.createUser(user);
	}

	public ClientAndUserDetailsService(ClientDetailsService clients,
			UserDetailsManager users) {
		super();
		clients_ = clients;
		users_ = users;
		clientDetailsWrapper_ = new ClientDetailsUserDetailsService(clients_);
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId)
			throws ClientRegistrationException {
		return clients_.loadClientByClientId(clientId);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		UserDetails user = null;
		try{
			user = users_.loadUserByUsername(username);
		}catch(UsernameNotFoundException e){
			user = clientDetailsWrapper_.loadUserByUsername(username);
		}
		return user;
	}
	
	

}
