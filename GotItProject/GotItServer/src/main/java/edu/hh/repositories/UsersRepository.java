package edu.hh.repositories;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.hh.auth.User;

/**
 * Authentication user repository for log-in. This is not a repository
 * with data for teens or followers, only for credentials.
 * 
 * 
 * @author Harold
 *
 */
@Repository
public interface UsersRepository extends CrudRepository<User, Long> {
	//Find all stored users with certain username, which should always
	//return a single result.
	public Collection<User> findByUsername(String username);
	
	public User findFirstByUsername(String username);
}
