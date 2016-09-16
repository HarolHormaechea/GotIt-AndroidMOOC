package edu.hh.repositories;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.hh.datamodel.FollowElement;
import edu.hh.datamodel.UserData;

/**
 * Repository to keep track of who follows who in this mess
 * of program!
 * 
 * @author Harold
 *
 */
@Repository
public interface FollowStatusRepository extends CrudRepository<FollowElement, Long>{
	public Collection<FollowElement> findByUserFollowing(UserData userFollowing);
	public Collection<FollowElement> findByUserFollowed(UserData userFollowed);
	
	//Only one of these should exist at any given time:
	public FollowElement findFirstByUserFollowedAndUserFollowing(UserData teenUser, UserData followerUser);
}
