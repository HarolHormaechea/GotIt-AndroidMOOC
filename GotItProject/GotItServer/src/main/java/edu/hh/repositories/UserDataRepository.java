package edu.hh.repositories;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.hh.datamodel.UserData;

/**
 * Repository for teen objects!
 * 
 * 
 * @author Harold
 *
 */
@Repository
public interface UserDataRepository extends CrudRepository<UserData, Long> {
	//Find all stored teens with a given username.. which should
	//always be only one.
	public Collection<UserData> findByUsername(String username);
	
	public UserData findFirstByUsername(String username);
}